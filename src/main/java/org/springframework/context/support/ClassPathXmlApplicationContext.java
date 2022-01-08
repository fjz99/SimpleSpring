package org.springframework.context.support;

/**
 * @date 2022/1/8 16:14
 */
public class ClassPathXmlApplicationContext extends AbstractXmlApplicationContext {
    private final String[] locations;

    public ClassPathXmlApplicationContext(String[] locations) {
        this.locations = locations;
        refresh ();
    }

    public ClassPathXmlApplicationContext(String location) {
        this (new String[]{location});
    }

    @Override
    protected String[] getConfigLocations() {
        return locations;
    }

}
