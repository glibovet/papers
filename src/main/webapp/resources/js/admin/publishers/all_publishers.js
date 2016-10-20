(function(){

    const LIMIT = 10;
    const FIELDS = 'fields=id,title,url';

    var app = angular.module('all_publishers', ['ui-notification']);

    app.controller('all_publishers', function($scope, $http, Notification){
        $scope.filters = { };

        getPublishers($scope, $http, Notification);
        countPublishers($scope, $http);

        $scope.deletePublisher = function(p){
            deletePublisher(p, $scope, $http, Notification);
        };

        $scope.filterPublishers = function(){
            getPublishers($scope, $http, Notification);
            countPublishers($scope, $http);
        };
    });


    function getPublishers($scope, $http, Notification){
        var page = $scope.filters.page || 0;
        var offset = page * LIMIT;

        $scope.publishers = [];

        $http.get('/api/publishers/?' + restrict($scope) + '&limit=' + LIMIT + '&offset=' + offset + '&' + FIELDS)
            .then(function(response){
                if (response.data.error) {
                    Notification({message: errorMessage(response.data.error)}, 'error');
                } else {
                    $scope.publishers = response.data.result;
                }
            });
    }

    function countPublishers($scope, $http){
        $http.get('/api/publishers/count?' + restrict($scope))
            .then(function(response){
                var pages = angular.element('#pages');
                var page = $scope.filters.page || 0;
                var numberOfPublishers = response.data.result || 1;

                pages.empty();

                var i = 0;
                do {
                    pages.append(['<option value="',i,'">',i+1,'</option>'].join(''));
                    ++i;
                    numberOfPublishers -= LIMIT;
                } while(numberOfPublishers >= LIMIT);

                pages.val(page);
            });
    }

    function restrict($scope){
        var f = $scope.filters;
        return 'restrict=' + JSON.stringify({query: valid(f.query)});
    }

    function valid(val){
        if(!val)
            return null;
        return val;
    }

    function deletePublisher(publisher, $scope, $http, Notification){
        if (confirm(messages_admin['admin.delete.approve'])) {
            $http.delete('/api/publishers/'+publisher.id, {headers: HEADERS})
                .then(function(response){
                    if (response.data.error) {
                        Notification({message: error(response.data.error)}, 'error');
                    } else {
                        Notification({message: messages_admin['admin.deleted']}, 'success');
                        var index = $scope.publishers.indexOf(publisher);
                        if (index > -1) {
                            $scope.publishers.splice(index, 1);
                        }
                    }
                });
        }
    }
})();
