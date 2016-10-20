var app = angular.module('publisher_edit', ['ui-notification']);

app.controller('publisher_controller', function($scope, $http, Notification){
     var params = UrlUtil.parse(angular.element('#loader').attr('src'));
    params.id = parseInt(params.id);

    if (params.id) {
        $http.get('/api/publishers/'+params.id+'?fields=id,title,description,url,contacts')
            .then(function(response){
                if (response.data.error) {
                    Notification({message: errorMessage(response.data.error)}, 'error');
                    setTimeout(function(){
                        location.href = '/admin/publishers/all';
                    }, 3000);
                } else {
                    $scope.publisher = response.data.result;
                }
            }, function(xhr){
                console.log(xhr);
                Notification({message: messages_admin['admin.ajax.error']}, 'error');
            });
    } else {
        $scope.publisher = {};
    }
});

app.controller('publisher_edit', function($scope, $http, Notification){
    $scope.editPublisherForm = function(){
        var publisher = $scope.$parent.publisher;
        var _method = (publisher.id ? $http.post : $http.put);

        _method('/api/publishers/', JSON.stringify(publisher), {headers: HEADERS})
            .then(function(response){
                if (response.data.result) {
                    Notification({message: messages_admin['admin.saved']}, 'success');
                    if (!publisher.id) {
                        setTimeout(function(){
                            location.href = '/admin/publishers/'+response.data.result+'/edit';
                        }, 2000);
                    }
                } else {
                    Notification({message: errorMessage(response.data.error)}, 'error');
                }
            }, function(xhr){
                console.log(xhr);
                Notification({message: messages_admin['admin.ajax.error']}, 'error');
            })
    };
});