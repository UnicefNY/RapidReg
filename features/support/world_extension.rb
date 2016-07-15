Dir.glob(File.join(File.dirname(__FILE__),'pages','**/*.rb')) {|file| require_relative file}

module AndroidPageDomain
  def test_account
    json = File.read(File.join(File.dirname(__FILE__),'..','test_data','test_account.json'))
    @account =  JSON.parse(json)
  end

  def test_case
    json = File.read(File.join(File.dirname(__FILE__),'..','test_data','test_case.json'))
    @account = JSON.parse(json)
  end

  def base_page
    @basePage = RapidRegAppPage.new
  end

  def login_page
    @loginPage = Screen::Android::LoginPage.new
  end

  def main_menu
    @mainMenu = Screen::Android::MainMenu.new
  end

  def case_page
    @casePage = Screen::Android::CasePage.new
  end

  def base_form
    @baseForm = Screen::Android::BaseForm.new
  end
end

World(AndroidPageDomain)

module WithinHelpers
  def with_scope(locator)
    locator ? within(locator) { yield } : yield
  end
end

World(WithinHelpers)
