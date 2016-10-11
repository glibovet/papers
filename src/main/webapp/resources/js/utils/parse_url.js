(function(exports){

    /**
     * @description create json based on query params
     *
     * @param url - url to be parsed
     */
    exports.parse = function(url){
        if(!url)
            return {};
        var index = url.indexOf('?');
        if(index > -1){
            var result = {};
            var query = url.substr(index + 1);

            query.split('&').forEach(function(param){
                var array = param.split('=');

                result[array[0]] = array[1];
            });

            return result;
        }
        return {};
    };


})(window.UrlUtil = {});