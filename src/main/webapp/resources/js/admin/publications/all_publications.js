(function(){

    const LIMIT = 20;
    const FIELDS = 'fields=id,title,type,annotation,authors_id,in_index,status';

    var app = angular.module('all_publications', ['ui-notification', 'autocomplete']);

    app.controller('all_publications', function($scope, $http, Notification){
        $scope.filters = { };

        getPublications($scope, $http, Notification);
        countPublications($scope, $http);

        $scope.deletePublication = function(p){
            deletePublication(p, $scope, $http, Notification);
        };

        $scope.filterPublications = function(){
            getPublications($scope, $http, Notification);
            countPublications($scope, $http);
        };

        $scope.setStatus = function(publication) {
            setStatus(publication, $http, Notification);
        };

        $scope.indexPublication = function(publication) {
            indexPublication(publication, $http, Notification);
        };

        $scope.createIndex = function() {
            createIndex($http, Notification);
        };

        $scope.deleteIndex = function() {
            deleteIndex($scope, $http, Notification);
        };

        $scope.indexAll = function() {
            indexAll($scope, $http, Notification);
        };

        authorAutocompete($scope, $http, Notification);
    });


    function getPublications($scope, $http, Notification){
        var page = $scope.filters.page || 0;
        var offset = page * LIMIT;

        $scope.publications = [];

        $http.get('/api/publication/?restrict=' + UrlUtil.encode($scope.filters) + '&limit=' + LIMIT + '&offset=' + offset + '&' + FIELDS)
            .then(function(response){
                if (response.data.error) {
                    Notification({message: errorMessage(response.data.error)}, 'error');
                } else {
                    $scope.publications = response.data.result;

                    $scope.publications.forEach(function(p){
                        p.status_m = p.status.toLowerCase();
                        getAuthors(p, $http);
                        hasFile(p, $http);
                    });
                }
            });
    }

    function countPublications($scope, $http){
        $http.get('/api/publication/count?restrict=' + UrlUtil.encode($scope.filters))
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

    function deletePublication(publication, $scope, $http, Notification){
        if (confirm(messages_admin['admin.delete.approve'])) {
            $http.delete('/api/publication/'+publication.id, {headers: HEADERS})
                .then(function(response){
                    if (response.data.error) {
                        Notification({message: errorMessage(response.data.error)}, 'error');
                    } else {
                        Notification({message: messages_admin['admin.deleted']}, 'success');
                        var index = $scope.publications.indexOf(publication);
                        if (index > -1) {
                            $scope.publications.splice(index, 1);
                        }
                    }
                });
        }
    }

    function getAuthors(publication, $http) {
        var ids = publication.authors_id;

        $http.get('/api/authors/master/?fields=id,last_name,initials&restrict=' + UrlUtil.encode({ids: ids}))
            .then(function(response){
                if (response.data.result) {
                    publication.authors = response.data.result;
                }
            });
    }

    function setStatus(publication, $http, Notification) {
        $http.post('/api/publication/', JSON.stringify({id: publication.id, status: publication.status}), {headers: HEADERS})
            .then(function(response){
                if (response.data.result) {
                    Notification({message: messages_admin['admin.saved']}, 'success');
                    publication.status_m = publication.status.toLowerCase();
                } else {
                    Notification({message: errorMessage(response.data.error)}, 'error');
                }
            }, function(xhr){
                console.log(xhr);
                Notification({message: messages_admin['admin.ajax.error']}, 'error');
            })
    }

    function indexPublication(publication, $http, Notification) {
        Notification({message: 'індексується публікація: [' + publication.title + ']'}, 'info');
        $http.post('/api/elastic/publication/' + publication.id + '/index', {}, {headers: HEADERS})
            .then(function(response){
                if (response.data.result) {
                    Notification({message: 'публікація проіндексована: [' + publication.title + ']'}, 'success');
                    publication.in_index = true;
                } else {
                    Notification({message: errorMessage(response.data.error)}, 'error');
                }
            }, function(xhr){
                console.log(xhr);
                Notification({message: messages_admin['admin.ajax.error']}, 'error');
            })
    }

    function hasFile(publication, $http) {
        $http.get('/api/storage/paper/' + publication.id + '/has_file')
            .then(function(response){
                publication.has_file = !!response.data.result;
            }, function(xhr){
                console.log(xhr);
                Notification({message: messages_admin['admin.ajax.error']}, 'error');
            });
    }

    function authorAutocompete($scope, $http, Notification) {
        $scope.authors_autocompete = [];

        $scope.authorType = function(val) {
            $http.get('/api/authors/master/?fields=id,last_name,initials&restrict=' + UrlUtil.encode({query: val}))
                .then(function(response){
                    if (response.data.result) {
                        $scope.authors_autocompete = [];
                        response.data.result.forEach(function(e){
                            $scope.authors_autocompete.push(e.last_name + ' ' + e.initials + ' id=' + e.id);
                        });
                    }
                });
        };

        $scope.authorSelect = function(selected) {
            var array = /[\w\W\s\.]+\s+id=(\d+)/.exec(selected);
            var id = array[1];

            $scope.filters.authors_id = [id];
        };
    }

    function createIndex($http, Notification) {
        Notification({message: 'почато створення індекса'}, 'info');
        $http.post('/api/elastic/index', {}, {headers: HEADERS})
            .then(function (response) {
                if (response.data.error) {
                    Notification({message: errorMessage(response.data.error)}, 'error');
                } else {
                    Notification({message: 'створений новий індекс'}, 'success');
                }
            });
    }

    function deleteIndex($scope, $http, Notification) {
        if (confirm('Ви точно бажаєте видалити індекс')) {
            Notification({message: 'почато видалення індекса'}, 'info');
            $http.delete('/api/elastic/index', {headers: HEADERS})
                .then(function (response) {
                    if (response.data.error) {
                        Notification({message: errorMessage(response.data.error)}, 'error');
                    } else {
                        Notification({message: 'індекс видалено'}, 'success');
                        if ($scope.publications) {
                            $scope.publications.forEach(function (p) {
                                p.in_index = false;
                            });
                        }
                    }
                });
        }
    }

    function indexAll($scope, $http, Notification) {
        Notification({message: 'почата індексація всіх публікацій'}, 'info');
        $http.post('/api/elastic/index_all', {}, {headers: HEADERS})
            .then(function(response){
                if (response.data.result) {
                    Notification({message: 'закінчено індексування публікацій'}, 'success');
                    if ($scope.publications) {
                        $scope.publications.forEach(function(p){
                            p.in_index = true;
                        });
                    }
                } else {
                    Notification({message: errorMessage(response.data.error)}, 'error');
                }
            }, function(xhr){
                console.log(xhr);
                Notification({message: messages_admin['admin.ajax.error']}, 'error');
            })
    }
})();
