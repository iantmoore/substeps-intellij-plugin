
package uk.co.itmoore.intellisubsteps.ui;


public abstract class TestInfo {//implements PsiLocator {
//  public static final Map<String,Class> KNOWN_PSI_LOCATOR_CLASSES = new HashMap<String, Class>();
//  static {
//    KNOWN_PSI_LOCATOR_CLASSES.put(PoolOfTestTypes.TEST_METHOD, TestCaseInfo.class);
//    KNOWN_PSI_LOCATOR_CLASSES.put(PoolOfTestTypes.TEST_CLASS, TestClassInfo.class);
//    KNOWN_PSI_LOCATOR_CLASSES.put(PoolOfTestTypes.ALL_IN_PACKAGE, AllInPackageInfo.class);
//  }

//  @NotNull
//  public static TestInfo readInfoFrom(final ObjectReader reader) {
//    final String testType = reader.readLimitedString();
//    Class infoClass = KNOWN_PSI_LOCATOR_CLASSES.get(testType);
//    if (infoClass == null)
//      infoClass = DefaultTestInfo.class;
//    final TestInfo info;
//    try {
//      info = (TestInfo)  infoClass.newInstance();
//    } catch (Exception e) {
//      throw new RuntimeException(e);
//    }
//    info.readFrom(reader);
//    info.setTestCount(reader.readInt());
//    return info;
//  }

  private int myTestCount;

  public boolean shouldRun() {
    return false;
  }

  public int getTestsCount() {
    return myTestCount;
  }

  public void setTestCount(final int testCount) {
    myTestCount = testCount;
  }

//  public abstract void readFrom(ObjectReader reader);

  public abstract String getComment();

  public abstract String getName();

}
