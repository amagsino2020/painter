/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package painters;

import java.awt.Rectangle;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Allura
 */
public class PaintersTest3 {
     @Test
    public void testIsRect() {
        Rectangle rec = new Rectangle(5,7);
        Rectangle  rec2 = new Rectangle (9,9);
        Rectangle  rec3 = new Rectangle (2,4);
        double area1 = 35;
        double area2 = 81;
        double area3 = 8;
        
        assertEquals(area1,rec.getWidth()*rec.getHeight(),.0001);
        assertEquals(area2,rec2.getWidth()*rec2.getHeight(),.0001);
        assertEquals(area3,rec3.getWidth()*rec3.getHeight(),.0001);
    }
}
