Dir.glob(File.join(File.dirname(__FILE__),'pages','**/*.rb')) {|file| require_relative file}

module AndroidPageDomain

  def base_page
    @basePage = Screen::Android::BasePage.new
  end

  def login_page
    @loginPage = Screen::Android::LoginPage.new
  end

end

World(AndroidPageDomain)