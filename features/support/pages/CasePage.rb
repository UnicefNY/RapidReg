module Screen
  module Android
    class CasePage < RapidRegAppPage

      def createNewCase
        clickById("add_case")
      end

      def scrollToNextForm
        1.times { scrollPartScreen("left") }
      end

      def scrollToNextFiedls
        1.times { scrollPartScreen("down")}
        sleep 3
      end

      def saveCase
        clickById("save_case")
      end

    end
  end
end


