module Screen
  module Android
    class LoginPage < RapidRegAppPage

      def login(account)
        p account
        raise("Account dose not exist...") if account.empty?
        username = account["username"]
        password = account["password"]
        url = account["url"]
        loginAs(username,password,url)
      end

      def loginAs(username, password,url)
        set_login_username(username)
        set_login_password(password)
        set_login_url(url)
        sign_in
        puts "Log in..."
      end

      def reLoginAs(username, password)
        set_login_username(username)
        set_login_password(password)
        sign_in
        puts "Log in..."
      end

      def logout
        clickById("logout_label")
      end

      def getCurrentUser
        findById("login_user_label").text
      end

      def getUserOrganization
        findById("organization").text
      end

      def set_login_username(username)
        findById("username").send_keys("#{username}")
      end

      def set_login_password(password)
        findById("password").send_keys("#{password}")
      end

      def set_login_url(url)
        findById("url").send_keys("#{url}")
      end

      def sign_in
        clickById("login")
      end

    end
  end
end
