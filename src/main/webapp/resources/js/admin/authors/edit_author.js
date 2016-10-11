var app = angular.module('author_edit', ['ui-notification']);

app.controller('author_controller', function($scope, $http, Notification){

    var params = UrlUtil.parse(angular.element('#loader').attr('src'));
    params.id = parseInt(params.id);

    if (params.id) {
        $http.get('/api/authors/master/' + params.id + '?fields=id,last_name,initials')
            .then(function(response){
                if (response.data.result) {
                    $scope.author = response.data.result;
                } else {
                    Notification({message: errorMessage(response.data.error)}, 'error');
                    setTimeout(function(){
                        location.href = '/admin/authors/all';
                    }, 5000);
                }
            }, function(xhr){
                console.log(xhr);
                Notification({message: messages_admin['admin.ajax.error']}, 'error');
            });
    } else {
        $scope.author = { };
    }

});

app.controller('author_master_edit_controller', function($scope, $http, Notification){
    $scope.authorMasterSave = function(){
        var _method = ($scope.author.id ? $http.post : $http.put);

        _method('/api/authors/master/', JSON.stringify($scope.author), {headers: HEADERS})
            .then(function(response){
                var data = response.data;
                if (data.result) {
                    Notification({message: messages_admin['admin.saved']}, 'success');
                    if(!$scope.author.id){
                        setTimeout(function(){
                            location.href = '/admin/authors/' + data.result + '/edit';
                        }, 2000);
                    }
                } else {
                    Notification({message: errorMessage(data.error)}, 'error');
                }
            }, function(xhr){
                console.log(xhr);
                Notification({message: messages_admin['admin.ajax.error']}, 'error');
            });
    };
});

app.controller('sub_authors', function($scope){
     // just for communicating between sub modules
});

app.controller('sub_author_edit', function($scope, $http, Notification){
    var root = $scope.$parent.$parent;
    $scope.author_edit = { };

    $scope.subAuthorSave = function(){
        $scope.author_edit.master_id = root.author.id;
        $http.put('/api/authors/', JSON.stringify($scope.author_edit), {headers: HEADERS})
            .then(function(response){
                console.log(response);
            }, function(xhr){
                console.log(xhr);
            });
    };
});

function errorMessage(e){
    var result = e.message;

    var errors = e.errors;
    if (errors && errors.length > 0) {
        result += '<br />[';
        for (var i = 0; i < errors.length - 1; ++i) {
            result += errors[i];
        }
        result += errors[errors.length - 1];
        result += ']';
    }

    return result;
}