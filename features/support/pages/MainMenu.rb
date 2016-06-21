module Screen
  module Android
    class MainMenu < RapidRegAppPage

      def showMainMenu
        scrollFullScreen("right")
      end

      def hideMainMenu
        scrollFullScreen("left")
      end

      def pressMenuTab(tabname)
        clickByXpath("//android.widget.CheckedTextView[@text='#{tabname}']")
      end

      def pressMenuButton
        clickByXpath("//android.widget.ImageButton[@text='']")
      end

      def getPageTitle
        return findByXpath("//android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/
android.support.v4.widget.DrawerLayout/android.widget.LinearLayout/android.view.View/android.widget.TextView").text
      end
    end
  end
end


