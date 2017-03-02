module Screen
  module Android
    class LoginPage < RapidRegAppPage

      def login(account)
        raise("Account dose not exist...") if account.empty?
        username = account["username"]
        password = account["password"]
        url = account["url"]
        p account["username"]
        loginAs(username,password,url)
      end

      def loginAs(username, password,url)
        set_login_username(username)
        set_login_password(password)
        change_url
        sleep 1
        set_login_url(url)
        sign_in
        puts "Log in..."
      end

      def reLoginAs(username, password,url)
        set_login_username(username)
        set_login_password(password)
        change_url
        sleep 1
        set_login_url(url)
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

      def change_url
        clickById("change_url")
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
