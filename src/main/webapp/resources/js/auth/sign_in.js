(function(exports){

    var sing_in_popup_template = new EJS({url: '/resources/template/auth/sign_in_popup.ejs'});

    $(function(){
        $('#sign_in').click(renderSignInPopup);

        $('#logout').click(function(){
            Ajax.post({
                url: '/api/users/logout',
                success: function(response){
                    location.reload(true);
                }
            })
        });
    });

    function renderSignInPopup(){
        var popup = $(sing_in_popup_template.render({

        }));

        popup.find('.close').click(function(){
            popup.remove();
        });

        popup.find('#sign_in_form').submit(function(e){
            e.preventDefault();

            var self = $(this);

            Ajax.post({
                url: '/api/users/sign_in',
                data: JSON.stringify({
                    email: self.find('[name=email]').val(),
                    password: md5(self.find('[name=password]').val())
                }),
                success: function(response){
                    if(response.result){
                        location.reload(true);
                    } else if(response.error){
                        alert(response.error.message);
                    } else {
                        alert('service error');
                    }
                },
                error: function(xhr){
                    alert('service error');
                    console.log(xhr);
                }
            })
        });

        $('body').append(popup);
    }

})(window);