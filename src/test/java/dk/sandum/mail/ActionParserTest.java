/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dk.sandum.mail;

import junit.framework.TestCase;

/**
 *
 * @author osa
 */
public class ActionParserTest extends TestCase {
    
    public ActionParserTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testParse1() {
        MailDeliveryAction ac = MailDeliveryAction.parse("failed");
    }

    public void testParse2() {
        MailDeliveryAction ac = MailDeliveryAction.parse("Failed");
    }
}
