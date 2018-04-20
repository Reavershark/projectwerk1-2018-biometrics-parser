/*
 * The MIT License
 *
 * Copyright (c) 2018 Jonas Meeuws, Jonas Van Dycke
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package biometricsparser;

import chatservice.MqttChatService;
import com.fazecast.jSerialComm.SerialPort;
import com.google.gson.Gson;

public class BiometricsParser {

    private Gson gson = new Gson();
    private MqttChatService chatService = new MqttChatService();
    private SerialPort tty;

    private int baudRate = 115200;
    private int deviceSleepTime = 500;
    private int readSleepTime = 500;
    private int minimumBytes = 25;
    
    public BiometricsParser() {
        
        initialize();
        
        try {
            while (true) {
                while (tty.bytesAvailable() < minimumBytes) {
                    Thread.sleep(readSleepTime);
                }
                String message = readMessage();

                BiometricData biometricData = null;
                try {biometricData = Parser.parse(message);}
                catch (Exception e) {}

                sendMessage(biometricData);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        tty.closePort();
    }
    
    private void initialize() {
        while (SerialPort.getCommPorts().length == 0) {
            System.out.println("No device attatched, waiting for device");
            try {Thread.sleep(deviceSleepTime);}
            catch (InterruptedException e) {}
        }
        tty = SerialPort.getCommPorts()[0];
        tty.setBaudRate(baudRate);
        tty.openPort();
    }

    private String readMessage() {
        byte[] readBuffer = new byte[tty.bytesAvailable()];
        tty.readBytes(readBuffer, readBuffer.length);
        return new String(readBuffer);
    }
    
    private void sendMessage(BiometricData biometricData) {
        String jsonMessage = gson.toJson(biometricData);
        System.out.println(jsonMessage);
        chatService.sendMessage(jsonMessage);
    }
}
