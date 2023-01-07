package com.bbailey.smarthome.zwave.protocol;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.bbailey.smarthome.zwave.api.ApiStarted;
import com.bbailey.smarthome.zwave.api.ApiStarted.WakeupReason;
import com.bbailey.smarthome.zwave.api.ApplicationCommandHandler;
import com.bbailey.smarthome.zwave.api.ApplicationUpdate;
import com.bbailey.smarthome.zwave.api.GetControllerCapabilities;
import com.bbailey.smarthome.zwave.api.GetControllerCapabilities.ControllerCapabilities;
import com.bbailey.smarthome.zwave.api.GetLibraryVersion;
import com.bbailey.smarthome.zwave.api.GetNetworkIds;
import com.bbailey.smarthome.zwave.commandclass.CommandClasses;
import com.bbailey.smarthome.zwave.device.BasicDeviceType;
import com.bbailey.smarthome.zwave.device.GenericDeviceType;
import com.bbailey.smarthome.zwave.device.SpecificDeviceType;
import com.bbailey.smarthome.zwave.protocol.DataFrame.CommandType;
import com.bbailey.smarthome.zwave.utils.BitUtils;

public class ZwaveFrameTest {

    

    
    @Test
    public void testSerializeFrame() {
    	
    	DataFrame frame = new DataFrame(new GetLibraryVersion.Request());
    	byte[] packet = frame.serialize();
    	packet = packet;
    }
    

    
    @Test
    public void testDeserializeGetNwtworkIds() {
    	
    	byte[] packet = new byte[] { 0x01,0x20,(byte)0xC6,0x24,(byte)0xF7,0x62,0x01,(byte)0xA0 };
    	
    	DataFrame dataFrame = new DataFrame(0x08, packet);
    }
    
    
    @Test
    public void testGetLibraryVersion() {
    	
    	byte[] packet = new byte[] { 0x01,0x15,0x5A,0x2D,0x57,0x61,0x76,0x65,0x20,0x37,0x2E,0x31,0x35,0x07,(byte)0x93,0x00 };
    	
    	DataFrame frame = new DataFrame(0x10, packet);
    	assertEquals(0x00, frame.calculateChecksum());
    	
    	GetLibraryVersion.Response commandResponse = new GetLibraryVersion.Response(frame.getCommandPayload());
    	assertEquals(7, commandResponse.getLibraryVersion().getMajorVersion());
    	assertEquals(15, commandResponse.getLibraryVersion().getMinorVersion().get());
    	
    }
    
    
    @Test
    public void testGetNetworkIds() {
    	
    	byte packet[] = new byte[] { 0x01,0x20,(byte)0xC6,0x24,(byte)0xF7,0x62,0x01,(byte)0xA0 };
    	
    	DataFrame frame = new DataFrame(0x08, packet);
    	assertEquals((byte)0xA0, frame.calculateChecksum());
    	assertEquals(GetNetworkIds.COMMAND_ID, frame.getCommandId());
    	GetNetworkIds.Response response = new GetNetworkIds.Response(frame.getCommandPayload());
    	
    	assertEquals(1, response.getNodeId());
    	// Is this right???
    	assertEquals(-970655902, response.getHomeId());
    }
    
    
    @Test
    public void testGetControllerCapabilities() {
    	
    	byte packet[] = new byte[] { 0x01,0x05,0x3C,(byte)0xC3 };
    	
    	DataFrame frame = new DataFrame(0x04, packet);
    	assertEquals((byte)0xC3, frame.calculateChecksum());
    	assertEquals(GetControllerCapabilities.COMMAND_ID, frame.getCommandId());
    	
    	GetControllerCapabilities.Response response = new GetControllerCapabilities.Response(frame.getCommandPayload());
    	assertTrue(response.getCapabilities().contains(ControllerCapabilities.SECONDARY_CONTROLLER));
    	assertTrue(response.getCapabilities().contains(ControllerCapabilities.SUC_ENABLED));
    	assertTrue(response.getCapabilities().contains(ControllerCapabilities.NO_NODES_INCLUDED));
    	
    }
    
    
    @Test
    public void testApiStarted() {
    	
    	byte[] packet = new byte[] { 0x00,0x0A,0x03,0x00,0x03,0x02,0x07,0x06,0x5E,(byte)0x98, (byte)0x9F,0x6C,0x56,0x55,0x00,(byte)0xD0 };
    	
    	DataFrame frame = new DataFrame(0x10, packet);
    	assertEquals(CommandType.REQUEST, frame.getType());
    	assertEquals((byte)0xD0, (byte)frame.calculateChecksum());
    	assertEquals(ApiStarted.COMMAND_ID, frame.getCommandId());
    	
    	ApiStarted.Request request = new ApiStarted.Request(frame.getCommandPayload());

    	assertEquals(WakeupReason.WATCHDOG_RESET, request.getWakeupReason());
    	assertFalse(request.isWatchdogStarted());
    	assertEquals(0x03, request.getDeviceOptionMask());
    	assertEquals(GenericDeviceType.GENERIC_TYPE_STATIC_CONTROLLER, request.getGenericDeviceType());
    	assertEquals(SpecificDeviceType.SPECIFIC_TYPE_GATEWAY, request.getSpecificDeviceType());
    	assertTrue(request.getCommandClasses().contains(0x5E));
    	assertTrue(request.getCommandClasses().contains(0x98));
    	assertTrue(request.getCommandClasses().contains(0x9F));
    	assertTrue(request.getCommandClasses().contains(0x6C));
    	assertTrue(request.getCommandClasses().contains(0x56));
    	assertTrue(request.getCommandClasses().contains(0x55));
    	assertFalse(request.isSupportsLongRange());

    }
    
    @Test
    public void testApplicationCommandHandler() {
    	
    	byte[] packet = new byte[] {0x00,0x04,0x00,0x02,0x02,(byte)0x84,0x07,(byte)0xCE,0x00,0x00,(byte)0xBD};
    	
    	DataFrame frame = new DataFrame(0x0B, packet);
    	assertEquals(CommandType.REQUEST, frame.getType());
    	//assertEquals((byte)0xDB, (byte)frame.calculateChecksum());
    	assertEquals(ApplicationCommandHandler.COMMAND_ID, frame.getCommandId());
    	
    	ApplicationCommandHandler.Request request = new ApplicationCommandHandler.Request(frame.getCommandPayload());
    	assertEquals(2, request.getSourceNodeId());
    	assertFalse(request.getRssi().isBelowSensitivity());
    	assertFalse(request.getRssi().isReceiverSaturated());
    	assertFalse(request.getRssi().isRssiNotAvailable());
    	assertEquals(-50, request.getRssi().getRssiValue().get());
    }
    
    
    @Test
    public void testApplicationUpdate() {
    	
    	byte[] packet = new byte[] { 0x00,0x49,(byte)0x84,0x02,0x17,0x04,0x07,0x01,0x5E,0x22,(byte)0x85,0x59,
    			(byte)0x80,0x70,0x5A,0x7A,(byte)0x87,0x72,(byte)0x8E,0x71,0x73,
    			(byte)0x98,(byte)0x9F,0x31,0x6C,0x55,(byte)0x86,(byte)0x84,0x3C 
    		};
    	
    	DataFrame frame = new DataFrame(0x1D, packet);
    	assertEquals(CommandType.REQUEST, frame.getType());
    	assertEquals((byte)0x3C, (byte)frame.calculateChecksum());
    	assertEquals(ApplicationUpdate.COMMAND_ID, frame.getCommandId());
    	
    	ApplicationUpdate.Request request = new ApplicationUpdate.Request(frame.getCommandPayload());
    	assertEquals(ApplicationUpdate.EventType.UPDATE_STATE_NODE_INFO_RECEIVED, request.getEvent());
    	assertEquals(BasicDeviceType.BASIC_TYPE_ROUTING_SLAVE, request.getBasicDeviceClass());
    	assertEquals(GenericDeviceType.GENERIC_TYPE_SENSOR_NOTIFICATION, request.getGenericDeviceType());
    	assertEquals(SpecificDeviceType.SPECIFIC_TYPE_NOTIFICATION_SENSOR, request.getSpecificDeviceType());
    	
    	// Confirm the expected command classes are present
    	assertTrue(request.getSupportedCommands().contains(CommandClasses.COMMAND_CLASS_APPLICATION_STATUS));
    	assertTrue(request.getSupportedCommands().contains(CommandClasses.COMMAND_CLASS_ASSOCIATION));
    	assertTrue(request.getSupportedCommands().contains(CommandClasses.COMMAND_CLASS_ASSSOCIATION_GRP_INFO));
    	assertTrue(request.getSupportedCommands().contains(CommandClasses.COMMAND_CLASS_BATTERY));
    	assertTrue(request.getSupportedCommands().contains(CommandClasses.COMMAND_CLASS_CONFIGURATION));
    	assertTrue(request.getSupportedCommands().contains(CommandClasses.COMMAND_CLASS_DEVICE_RESET_LOCALLY));
    	assertTrue(request.getSupportedCommands().contains(CommandClasses.COMMAND_CLASS_FIRMWARE_UPDATE_MD));
    	assertTrue(request.getSupportedCommands().contains(CommandClasses.COMMAND_CLASS_INDICATOR));
    	assertTrue(request.getSupportedCommands().contains(CommandClasses.COMMAND_CLASS_MANUFACTURER_SPECIFIC));
    	//assertTrue(request.getSupportedCommands().contains(CommandClass.COMMAND_CLASS_MULTICHANNEL_ASSOCIATION_GRP_INFO));
    	assertTrue(request.getSupportedCommands().contains(CommandClasses.COMMAND_CLASS_NOTIFICATION));
    	assertTrue(request.getSupportedCommands().contains(CommandClasses.COMMAND_CLASS_POWERLEVEL));
    	assertTrue(request.getSupportedCommands().contains(CommandClasses.COMMAND_CLASS_SECURITY));
    	assertTrue(request.getSupportedCommands().contains(CommandClasses.COMMAND_CLASS_SECURITY_2));
    	assertTrue(request.getSupportedCommands().contains(CommandClasses.COMMAND_CLASS_SENSOR_MULTILEVEL));
    	assertTrue(request.getSupportedCommands().contains(CommandClasses.COMMAND_CLASS_SUPERVISION));
    	assertTrue(request.getSupportedCommands().contains(CommandClasses.COMMAND_CLASS_TRANSPORT_SERVICE));
    	assertTrue(request.getSupportedCommands().contains(CommandClasses.COMMAND_CLASS_VERSION));
    	assertTrue(request.getSupportedCommands().contains(CommandClasses.COMMAND_CLASS_WAKE_UP));
    	assertTrue(request.getSupportedCommands().contains(CommandClasses.COMMAND_CLASS_ZWAVEPLUS_INFO));
    }

}
