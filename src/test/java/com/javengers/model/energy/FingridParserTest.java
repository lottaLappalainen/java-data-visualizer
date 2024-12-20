package com.javengers.model.energy;

import com.javengers.model.datatypes.EnergyType;
import com.javengers.model.datatypes.TimeRange;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;


public class FingridParserTest {
    private static final Logger logger = Logger.getLogger(FingridParser.class.getName());
    
    public FingridParserTest() {
    }
    
    @BeforeAll
    public static void disableLogs() {
        logger.setLevel(Level.OFF);
    }
    
    @AfterAll
    public static void enableLogs() {
        logger.setLevel(Level.INFO);
    }
    
    /**
     * Tests the parseEnergyData method with the data parameter being an empty
     * string.
     */
    @Test
    public void testParseEnergyData_DataEmptyString() {
        String data = "";
        EnergyType energyType = EnergyType.SOLAR;
        TimeRange timeRange = TimeRange.CURRENT;
        FingridParser instance = new FingridParser();
        double[] result = instance.parseEnergyData(data, energyType, timeRange);
        assertNull(result,"Expected null");
    }
    
    /**
     * Tests the parseEnergyData method with the data parameter being null.
     */
    @Test
    public void testParseEnergyData_DataNull() {
        String data = null;
        EnergyType energyType = EnergyType.SOLAR;
        TimeRange timeRange = TimeRange.CURRENT;
        FingridParser instance = new FingridParser();
        double[] result = instance.parseEnergyData(data, energyType, timeRange);
        assertNull(result,"Expected null");
    }
    
    /**
     * Tests the parseEnergyData method with the data parameter containing an
     * empty "data" array.
     */
    @Test
    public void testParseEnergyData_DataEmpty() {
        String data = "{\"data\":[]}";
        EnergyType energyType = EnergyType.SOLAR;
        TimeRange timeRange = TimeRange.CURRENT;
        FingridParser instance = new FingridParser();
        double[] result = instance.parseEnergyData(data, energyType, timeRange);
        assertNull(result,"Expected null");
    }
}
