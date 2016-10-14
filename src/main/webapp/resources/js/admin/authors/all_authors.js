var app = angular.module('all_authors', ['ui-notification']);

app.controller('all_authors_ctrl', function($scope, $http, Notification){
    $http.get('/api/authors/master/?limit=10&offset=0&fields=id,last_name,initials')
        .then(function(response){
            var data = response.data;
            if(data.result){
                $scope.authors = data.result;
            } else {
                Notification({message: data.error.message}, 'error');
            }
        });

    $scope.deleteAuthor = function(author){
        if(confirm(messages_admin['admin.delete.approve'])){
            $http.delete('/api/authors/master/'+author.id, {headers: HEADERS})
                .then(function(response){
                    if(!response.data.error){
                        var index = $scope.authors.indexOf(author);
                        if(index > -1){
                            $scope.authors.splice(index, 1);
                        }
                        Notification({message: messages_admin['admin.deleted']}, 'success');
                    } else {
                        Notification({message: errorMessage(response.data.error)}, 'error');
                    }
                }, function(xhr){
                    console.log(xhr);
                    Notification({message: messages_admin['admin.ajax.error']}, 'error');
                })
        }
    };
});