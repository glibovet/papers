var app = angular.module('all_authors', ['ui-notification']);

const FIELDS = 'fields=id,last_name,initials';
const LIMIT = 20;

app.controller('all_authors_ctrl', function($scope, $http, Notification){
    $scope.filters = {

    };

    getAuthors();
    countAuthors();

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
                });
        }
    };

    $scope.filterAuthors = function(){
        getAuthors();
        countAuthors();
    };

    function getAuthors(){
        var query = '/api/authors/master/?';
        var offset = LIMIT * $scope.filters.page || 0;
        query += 'limit='+LIMIT+'&offset='+offset+'&'+FIELDS;
        query += '&' + restrict();

        $scope.authors = [];

        $http.get(query)
            .then(function(response){
                var data = response.data;

                if(data.result){
                    $scope.authors = data.result;
                } else {
                    Notification({message: data.error.message}, 'error');
                }
            });
    }

    function countAuthors(){
        var query = '/api/authors/master/count?'+restrict();

        $http.get(query)
            .then(function(response){
                var numberOfEntities = response.data.result || 1;
                var page = $scope.filters.page || 0;
                var element = angular.element('#pages');
                var i = 0;

                element.empty();
                do {
                    element.append(['<option value="', i, '">', i+1, '</option>'].join(''));
                    ++i;
                    numberOfEntities -= LIMIT;
                } while(numberOfEntities >= LIMIT);

                element.val(page);
            });
    }

    function restrict(){
        var f = $scope.filters;
        return 'restrict='+JSON.stringify({has_sub: valid(f.has_sub), query: valid(f.query)});
    }

    function valid(val){
        if(!val)
            return null;
        return val;
    }
});