(function(exports){

    var app = exports.app = angular.module('publication_edit', ['ui-notification', 'autocomplete', 'angularFileUpload']);

    app.controller('publication_controller', function($scope, $http, Notification, FileUploader){
        var params = UrlUtil.parse(angular.element('#loader').attr('src'));
        params.id = parseInt(params.id);

        if (params.id) {
            $http.get('/api/publication/'+params.id+'?fields=id,title,annotation,type,link,publisher_id,authors_id')
                .then(function(response){
                    if (response.data.error) {
                        Notification({message: errorMessage(response.data.error)}, 'error');
                        setTimeout(function(){
                            location.href = '/admin/publications/all';
                        }, 3000);
                    } else {
                        $scope.publication = response.data.result;
                        loadPublisher($scope, $http, Notification);
                        loadAuthors($scope, $http, Notification);
                        hasFile($scope.publication, $http);
                    }
                }, function(xhr){
                    console.log(xhr);
                    Notification({message: messages_admin['admin.ajax.error']}, 'error');
                });
        } else {
            $scope.publication = {
                authors_id: []
            };
        }

        savePublication($scope, $http, Notification);
        authorAutocompete($scope, $http, Notification);
        publisherAutocompete($scope, $http, Notification);
        initUploadFIleForm($scope, FileUploader, params.id, Notification);
    });

    function loadPublisher($scope, $http, Notification) {
        var id = $scope.publication.publisher_id;

        if (id) {
            $http.get('/api/publishers/' + id + '?fields=id,title')
                .then(function(response){
                    if (response.data.result) {
                        $scope.publisher = response.data.result;
                    } else {
                        Notification({message: errorMessage(response.data.error)}, 'error');
                    }
                }, function(xhr){
                    console.log(xhr);
                    Notification({message: messages_admin['admin.ajax.error']}, 'error');
                });
        }
    }

    function loadAuthors($scope, $http, Notification) {
        var ids = $scope.publication.authors_id;

        if (ids && ids.length) {
            $http.get('/api/authors/master/?fields=id,last_name,initials&restrict=' + UrlUtil.encode({ids: ids}))
                .then(function(response){
                    if (response.data.result) {
                        $scope.authors = response.data.result;
                    } else {
                        Notification({message: errorMessage(response.data.error)}, 'error');
                    }
                }, function(xhr){
                    console.log(xhr);
                    Notification({message: messages_admin['admin.ajax.error']}, 'error');
                });
        }
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

            $http.get('/api/authors/master/'+id+'?fields=id,last_name,initials')
                .then(function(response){
                    var author = response.data.result;

                    if (author) {
                        if ($scope.authors && $scope.authors.length) {
                            var added = false;
                            $scope.authors.some(function (a) {
                                if (author.id == a.id) {
                                    added = true;
                                    Notification({message: 'already added'}, 'warning');

                                    return false;
                                }
                            });

                            if (!added) {
                                $scope.authors.push(author);
                                $scope.publication.authors_id.push(author.id);
                            }
                        } else {
                            $scope.authors = [author];
                            $scope.publication.authors_id.push(author.id);
                        }
                    }
                });
        };

        $scope.deleteAuthorToPublication = function(author_id) {
            if ($scope.publication.id && false) {

            } else {
                var authors = $scope.authors;
                for (var i = 0; i < authors.length; ++i) {
                    if (authors[i].id == author_id) {
                        authors.splice(i, 1);
                        break;
                    }
                }
            }
        };
    }

    function publisherAutocompete($scope, $http) {
        $scope.publishers_autocompete = [];

        $scope.publisherType = function(val) {
            $http.get('/api/publishers/?fields=id,title&restrict=' + UrlUtil.encode({query: val}))
                .then(function(response){
                    if (response.data.result) {
                        $scope.publishers_autocompete = [];
                        response.data.result.forEach(function(e){
                            $scope.publishers_autocompete.push(e.title + ' id=' + e.id);
                        });
                    }
                });
        };

        $scope.publisherSelect = function(selected) {
            var array = /[\w\W\s\.]+\s+id=(\d+)/.exec(selected);
            var id = array[1];

            $http.get('/api/publishers/'+id+'?fields=id,title')
                .then(function(response){
                    var publisher = response.data.result;

                    if (publisher) {
                        $scope.publisher = publisher;
                        $scope.publication.publisher_id = publisher.id;
                    }
                });
        };
    }

    function savePublication($scope, $http, Notification) {
        $scope.editPublicationForm = function() {
            var _method = $scope.publication.id ? $http.post : $http.put;

            _method('/api/publication/', JSON.stringify($scope.publication), {headers: HEADERS})
                .then(function(response){
                    if (response.data.result) {
                        Notification({message: messages_admin['admin.saved']}, 'success');
                        if (!$scope.publication.id) {
                            setTimeout(function(){
                                location.href = '/admin/publications/'+response.data.result+'/edit';
                            }, 2000);
                        }
                    } else {
                        Notification({message: errorMessage(response.data.error)}, 'error');
                    }
                }, function(xhr){
                    console.log(xhr);
                    Notification({message: messages_admin['admin.ajax.error']}, 'error');
                })
        };
    }


    function initUploadFIleForm($scope, FileUploader, id, Notification) {
        var headers = angular.extend({}, HEADERS);
        delete headers['Content-Type'];
        delete headers['Accept'];

        var uploader = $scope.uploader = new FileUploader({
            url: '/api/storage/paper/' + id,
            headers: headers
        });

        uploader.onSuccessItem = function(item, response) {
            if (response.result) {
                Notification({message: 'file saved'}, 'success');
                $scope.publication.has_file = true;
            } else {
                Notification({message: errorMessage(response.error)}, 'error');
            }
        };

        uploader.onErrorItem = function(item, response) {
            if (response.result) {
                Notification({message: 'fail to save'}, 'error');
            } else {
                Notification({message: errorMessage(response.error)}, 'error');
            }
        };

        uploader.onBeforeUploadItem = function() {
            Notification({message: 'start uploading file..'}, 'info');
        };

        uploader.onProgressItem = function(item, progress) {
            if (progress >= 99) {
                Notification({message: 'processing file on server ..'}, 'info');
            }
        };
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
})(window);