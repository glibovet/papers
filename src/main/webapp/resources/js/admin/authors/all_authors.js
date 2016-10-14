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
});