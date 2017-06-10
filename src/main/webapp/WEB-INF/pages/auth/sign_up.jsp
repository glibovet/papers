<%--
  Created by IntelliJ IDEA.
  User: oleh_kurpiak
  Date: 05.09.2016
  Time: 12:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Реєстрація</title>
    <jsp:include page="../common/include_resources.jsp" />
    <link rel="stylesheet" href="/resources/css/sign_up.css">

    <script>
        $(document).ready(function(){
            $('#sign_up_form').submit(function(e){
                e.preventDefault();

                var self = $(this);
                var password = self.find('[name=password]').val();
                var repeat_password = self.find('[name=password_repeat]').val();

                if(password.length < 8){
                    alert('password should have at least 8 characters');
                    return;
                }

                if(password != repeat_password){
                    alert('passwords must be equals');
                    return;
                }

                Ajax.put({
                    url: '/api/users/',
                    data: JSON.stringify({
                        email: self.find('[name=email]').val(),
                        password: md5(password),
                        role: 'user'
                    }),
                    dataType: 'json',
                    success: function(response){
                        if(response.result){
                            location.href = '/';
                        } else if(response.error){
                            var error = response.error;
                            var message = error.message;
                            if(error.errors){
                                message += "[";
                                for(var i = 0; i < error.errors.length; ++i){
                                    message += error.errors[i] + ", ";
                                }
                                message += "]";
                            }
                            alert(message);

                        } else {
                            alert('service error');
                        }
                    },
                    error: function(xhr){
                        alert('service error');
                        console.log(xhr);
                    }
                });
            });
        });
    </script>
</head>
<body>
<div class="site-wrapper">
    <div class="site-wrapper-inner">
        <div class="cover-container">
            <jsp:include page="../common/header.jsp"/>

            <div class="inner cover">
                <form class="form-signin" id="sign_up_form">
                    <label class="sr-only">Електронна пошта</label>
                    <input type="email" name="email" class="form-control" placeholder="email@example.com">
                    <label class="sr-only">Пароль</label>
                    <input type="password" name="password" class="form-control" placeholder="My super secret password">
                    <label class="sr-only">Повторіть пароль</label>
                    <input type="password" name="password_repeat" class="form-control" placeholder="My super secret password(again)">
                    <button class="btn btn-lg btn-primary btn-block" type="submit">Зареєструватись</button>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
</body>
</html>

