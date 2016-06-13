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

    end
  end
end


