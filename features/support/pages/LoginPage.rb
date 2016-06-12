module Screen
  module Android
    class LoginPage < RapidRegAppPage

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
        clickByXpath("//android.widget.CheckedTextView[@text='Logout']")
      end

      def getCurrentUser
        findById("login_user_label")
      end

      def set_login_username(username)
        findById("editview_username").send_keys("#{username}")
      end

      def set_login_password(password)
        findById("editview_password").send_keys("#{password}")
      end

      def set_login_url(url)
        findById("editview_url").send_keys("#{url}")
      end

      def sign_in
        clickById("button_login")
      end

    end
  end
end

# type: # android.widget.CheckedTextView
# text: Logout

# login_user_label