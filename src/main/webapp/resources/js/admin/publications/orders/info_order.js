(function(exports){

    var app = exports.app = angular.module('publication_order', ['ui-notification']);

    app.controller('publication_order', function($scope, $http, Notification){
        var params = UrlUtil.parse(angular.element('#loader').attr('src'));
        params.id = parseInt(params.id);

        $http.get('/api/publication/order/'+params.id+'?fields=id,email,reason,answer,status,publication_id,date')
            .then(function(response){
                if (response.data.error) {
                    Notification({message: errorMessage(response.data.error)}, 'error');
                    setTimeout(function(){
                        location.href = '/admin/publications/orders/all';
                    }, 3000);
                } else {
                    $scope.order = response.data.result;
                    $scope.order.status = $scope.order.status.toLowerCase();
                    $scope.order.date = new Date($scope.order.date).format('dd.MM.yyyy HH:mm');
                    $scope.answer = $scope.order.answer;
                    loadPublication($scope, $http, Notification);
                }
            }, function(xhr){
                console.log(xhr);
                Notification({message: messages_admin['admin.ajax.error']}, 'error');
            });

        $scope.acceptOrder = function() {
            var id = $scope.order.id,
                status = 'APPLIED';

            $http.post('/api/publication/order/answer', JSON.stringify({id: id, status: status}), {headers: HEADERS})
                .then(function(response){
                    if (response.data.result) {
                        Notification({message: messages_admin['admin.saved']}, 'success');
                        $scope.order.status = status.toLowerCase();
                    } else {
                        Notification({message: errorMessage(response.data.error)}, 'error');
                    }
                }, function(xhr){
                    console.log(xhr);
                    Notification({message: messages_admin['admin.ajax.error']}, 'error');
                })
        };

        $scope.rejectOrder = function() {
            var id = $scope.order.id,
                answer = $scope.answer,
                status = 'REJECTED';

            if (!answer) {
                Notification({message: 'вкажіть причину відмови'}, 'warning');

                return;
            }

            $http.post('/api/publication/order/answer', JSON.stringify({id: id, status: status, answer: answer}), {headers: HEADERS})
                .then(function(response){
                    if (response.data.result) {
                        Notification({message: messages_admin['admin.saved']}, 'success');
                        $scope.order.status = status.toLowerCase();
                        $scope.order.answer = answer;
                    } else {
                        Notification({message: errorMessage(response.data.error)}, 'error');
                    }
                }, function(xhr){
                    console.log(xhr);
                    Notification({message: messages_admin['admin.ajax.error']}, 'error');
                })
        }
    });


    function loadPublication($scope, $http, Notification) {
        var id = $scope.order.publication_id;

        if (id) {
            $http.get('/api/publication/' + id + '?fields=title')
                .then(function(response){
                    if (response.data.result) {
                        $scope.order.publication_name = response.data.result.title;
                    } else {
                        Notification({message: errorMessage(response.data.error)}, 'error');
                    }
                }, function(xhr){
                    console.log(xhr);
                    Notification({message: messages_admin['admin.ajax.error']}, 'error');
                });
        }
    }

})(window);