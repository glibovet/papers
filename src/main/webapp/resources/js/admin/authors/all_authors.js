var app = angular.module('all_authors', ['ui-notification']);

const FIELDS = 'fields=id,last_name,initials';
const LIMIT = 20;

app.controller('all_authors_ctrl', function($scope, $http, Notification){
    $scope.filters = {

    };

    getAuthors();

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
                });
        }
    };

    $scope.filterAuthors = function(){
        getAuthors();
    };

    function getAuthors(){
        var query = '/api/authors/master/?';
        var offset = LIMIT * $scope.page || 0;
        query += 'limit='+LIMIT+'&offset='+offset+'&'+FIELDS;

        var f = $scope.filters;
        query += '&restrict='+JSON.stringify({has_sub: valid(f.has_sub), query: valid(f.query)});

        $http.get(query)
            .then(function(response){
                var data = response.data;
                if(data.result){
                    $scope.authors = data.result;
                } else {
                    $scope.authors = [];
                    Notification({message: data.error.message}, 'error');
                }
            });
    }

    function valid(val){
        if(!val)
            return null;
        return val;
    }
});