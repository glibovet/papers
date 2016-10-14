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

app.controller('sub_authors', function($scope, $http, Notification){
    $scope.sub_authors = [];
    $scope.author_edit = { };
    var root = $scope.$parent;

    $scope.subAuthorSave = function(){
        $scope.author_edit.master_id = root.author.id;

        var _method = $scope.author_edit.id ? $http.post : $http.put;

        _method('/api/authors/', JSON.stringify($scope.author_edit), {headers: HEADERS})
            .then(function(response){
                if(response.data.result){
                    $scope.author_edit = {};
                    Notification({message: messages_admin['admin.saved']}, 'success');
                    loadSubAuthors();
                } else {
                    Notification({message: errorMessage(response.data.error)}, 'error');
                }
            }, function(xhr){
                console.log(xhr);
                Notification({message: messages_admin['admin.ajax.error']}, 'error');
            });
    };

    $scope.editSubAuthor = function(a){
        $scope.author_edit = {
            id: a.id,
            initials: a.initials,
            last_name: a.last_name,
            original: a.original
        };
    };

    //load sub_authors after 1 second
    setTimeout(function(){
        loadSubAuthors();
    }, 1000);

    function loadSubAuthors(){
        $scope.sub_authors = [];

        var master_id = root.author.id;
        $http.get('/api/authors/?fields=id,last_name,initials,original&restrict='+JSON.stringify({master_ids: [master_id]}))
            .then(function(response){
                var authors = response.data.result;
                if(authors){
                    authors.forEach(function(a){
                        $scope.sub_authors.push(a);
                    });
                }
            });
    }
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