var app = angular.module('index', []);
app.controller('search_box', function($scope){
    $scope.startSearch = function(){
        angular.element('#search_result_list').show();
        $scope.result = [
            {name: 'морські котики.doc'},
            {name: 'котики_їх_шерсть.doc'},
            {name: 'sea fish.doc'}
        ];
    }
});