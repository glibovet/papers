(function(exports){

    var csrf = {};

    exports.setCSRF = function(name, val){
        csrf = {
            name: name,
            val: val
        };
    };


    exports.get = function(options){
        options = options || {};
        options.type = 'GET';
        _ajax(options);
    };

    exports.put = function(options){
        options = options || {};
        options.type = 'PUT';
        _ajax(options);
    };

    exports.post = function(options){
        options = options || {};
        options.type = 'POST';
        _ajax(options);
    };

    exports.delete = function(options){
        options = options || {};
        options.type = 'DELETE';
        _ajax(options);
    };

    function _ajax(options){
        options.beforeSend = function(xhr){
            xhr.setRequestHeader(csrf.name, csrf.val);
            xhr.setRequestHeader('Content-Type', 'application/json');
            xhr.setRequestHeader('Accept', 'application/json');
        };
        $.ajax(options);
    }

})(window.Ajax = {});