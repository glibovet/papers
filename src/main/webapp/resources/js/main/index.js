$(function(){

    var $authors_result = $('#search_result');

    $('#search_author').submit(function(e){
        e.preventDefault();

        $authors_result.empty();

        var restricts = {};

        $(this).find('.form-control').each(function(){
            var $self = $(this),
                val = $self.val();

            if (val) {
                restricts[$self.attr('name')] = val;
            }
        });

        Ajax.get({
            url: '/api/authors/find?fields=id,last_name,initials&restrict=' + JSON.stringify(restricts),
            success: function(response) {
                if (response.error) {
                    $authors_result.text('Нічого не знайдено');
                } else {
                    var text = [];
                    response.result.forEach(function(author){
                        text.push('<a href="/authors/' + author.id + '/view">' + author.last_name + ' ' + author.initials + '</a>');
                    });
                    $authors_result.html(text.join('<br />'));
                }
            }
        });
    });

});