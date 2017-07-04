(function(){

    var order_publication_popup = new EJS({url: '/resources/template/search/order_publication_popup.ejs'});

    $('.order-publication').click(function(){
        var id = $(this).data('id');

        var $popup = $(order_publication_popup.render({}));

        $popup.find('.order_form').submit(function(e){
            e.preventDefault();

            var $self = $(this);

            var data = {
                publication_id: id,
                email: $self.find('[name=email]').val(),
                reason: $self.find('[name=reason]').val()
            };

            if (!data.email || !data.reason) {
                alert('заповніть всі поля');

                return;
            }

            Ajax.put({
                url: '/api/publication/order/',
                data: JSON.stringify(data),
                success: function(response) {
                    if (response.result) {
                        $popup.find('.message').text('замовлення створено');
                    } else {
                        $popup.find('.message').text(response.error.message);
                    }
                }
            });
        });

        $popup.find('.close').click(function(){
            $popup.remove();
        });

        $('body').append($popup);
    });

})();