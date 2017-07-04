(function(){

    const LIMIT = 50;
    const FIELDS = 'fields=id,email,status,date';

    var app = angular.module('all_orders', ['ui-notification', 'autocomplete']);

    app.controller('all_orders', function($scope, $http, Notification){
        $scope.filters = { };

        getPublications($scope, $http, Notification);
        countPublications($scope, $http);

        $scope.filterOrders = function(){
            getPublications($scope, $http, Notification);
            countPublications($scope, $http);
        };

    });


    function getPublications($scope, $http, Notification){
        var page = $scope.filters.page || 0;
        var offset = page * LIMIT;

        $scope.orders = [];

        $http.get('/api/publication/order/?restrict=' + UrlUtil.encode($scope.filters) + '&limit=' + LIMIT + '&offset=' + offset + '&' + FIELDS)
            .then(function(response){
                if (response.data.error) {
                    Notification({message: errorMessage(response.data.error)}, 'error');
                } else {
                    $scope.orders = response.data.result;

                    $scope.orders.forEach(function(p){
                        p.status = p.status.toLowerCase();
                        p.date = new Date(p.date).format('dd.MM.yyyy HH:mm');
                    });
                }
            });
    }

    function countPublications($scope, $http){
        $http.get('/api/publication/order/count?restrict=' + UrlUtil.encode($scope.filters))
            .then(function(response){
                var pages = angular.element('#pages');
                var page = $scope.filters.page || 0;
                var numberOfPublications = response.data.result || 1;

                pages.empty();

                var i = 0;
                do {
                    pages.append(['<option value="',i,'">',i+1,'</option>'].join(''));
                    ++i;
                    numberOfPublications -= LIMIT;
                } while(numberOfPublications >= LIMIT);

                pages.val(page);
            });
    }

})();
