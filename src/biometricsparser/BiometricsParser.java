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

    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) {
        Gson gson = new Gson();
	MqttChatService chatService = new MqttChatService();
        
        SerialPort tty;
        try {
            tty = SerialPort.getCommPorts()[0];
        }
        catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("No device attatched");
	    //TODO: Close program
            return;
        }
        tty.setBaudRate(115200);
        tty.openPort();
        
        try {
            while (true) {
                while (tty.bytesAvailable() == 0) {
                    Thread.sleep(1000);
                }
                byte[] readBuffer = new byte[tty.bytesAvailable()];
                tty.readBytes(readBuffer, readBuffer.length);
                String message = new String(readBuffer);
		
                BiometricData biometricData = null;
                try {biometricData = Parser.parse(message);}
                catch (Exception e) {}
		
		String jsonMessage = gson.toJson(biometricData);
                System.out.println(jsonMessage);
		chatService.sendMessage(jsonMessage);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (NegativeArraySizeException e) {
            System.out.println("Device detached");
        } 
        tty.closePort();        
    }
    
}
