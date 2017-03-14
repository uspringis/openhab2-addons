package test;

import java.util.Set;

import org.code_house.bacnet4j.wrapper.api.BacNetClient;
import org.code_house.bacnet4j.wrapper.api.BypassBacnetConverter;
import org.code_house.bacnet4j.wrapper.api.Device;
import org.code_house.bacnet4j.wrapper.api.DeviceDiscoveryListener;
import org.code_house.bacnet4j.wrapper.api.Property;
import org.code_house.bacnet4j.wrapper.api.Type;
import org.code_house.bacnet4j.wrapper.ip.BacNetIpClient;

import com.serotonin.bacnet4j.type.Encodable;

public class MainTest {

    public static void main(String[] args) {
        BacNetClient client = null;
        try {
            client = new BacNetIpClient("0.0.0.0", "255.255.255.255", 1111);
            client.start();

            Set<Device> devices = client.discoverDevices(new DeviceDiscoveryListener() {

                @Override
                public void deviceDiscovered(Device device) {
                    System.out.println("Device found: " + device.toString());
                }
            }, 5000); // given number is timeout in millis
            System.out.println("Discovery completed");

            for (Device device : devices) {
                System.out.println("Device found: " + device.toString());

                // Number humidifier_mesured_value "Humidifier measured value"
                // {bacnet="device=1001,type=analogInput,id=6,refreshInterval=5"}
                if (device.getObjectIdentifier().getInstanceNumber() == 1001) {
                    Encodable v = client.getPropertyValue(
                            new Property(device, 10010, "name", "description", "units", Type.ANALOG_VALUE),
                            new BypassBacnetConverter());
                    System.out.println(v.toString());
                }

            }

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        } finally {

            client.stop();
            System.out.println("Client stoped");
        }

    }

}
