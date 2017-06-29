package com.cloud4magic.freecast.api;

import android.util.Log;

/**
 *	DESCRIPTION:
 *	This class is used to realize intercom-speaking of module.
 *
 *	NOTE:
 *	If you have any problem,please send email to: steven.tang@rakwireless.com
 */
public class SendAudio {
    /**
     * Description: Change PCM data to PCMU data
     * 		data: Audio data (PCM Format)
     * 		length: Audio data length
     */
    public byte[] PCMToPCMU(byte[] data,int length)
    {
        byte[] PCMU_Data=new byte[length/2];
        for(int i=0;i<length/2;i++)
        {
            int v=data[i*2+1]*256+ data[i*2];
            PCMU_Data[i]=(byte)linear2ulaw(v);
        }
        return PCMU_Data;
    }

    /**
     * Description: Change PCM data to PCMU data
     */
    private int BIAS =0x84;
    private int[] seg_end= {0xFF, 0x1FF, 0x3FF, 0x7FF,0xFFF, 0x1FFF, 0x3FFF, 0x7FFF};
    private char linear2ulaw(int  pcm_val)
    {
        int  mask;
        int  seg;
        char uval;

        if (pcm_val < 0)
        {
            pcm_val = BIAS - pcm_val;
            mask = 0x7F;
        }
        else
        {
            pcm_val += BIAS;
            mask = 0xFF;
        }
        seg = search(pcm_val, seg_end, 8);
        if (seg >= 8)
            return (char) (0x7F ^ mask);
        else
        {
            uval = (char) ((seg << 4) | ((pcm_val >> (seg + 3)) & 0xF));
            return (char) (uval ^ mask);
        }
    }

    int search(int  val,int[] table,int  size)
    {
        int  i;
        for (i = 0; i < size; i++)
        {
            if (val <= table[i])
                return (i);
        }
        return (size);
    }

    /**
     * Description:Send PCMU data to module
     * 		ip:   module ip
     * 		port: module port
     * 		buf: Audio data (PCMU Format)
     * 		len: Audio data length
     */
    private TcpSocket audioSocket=null;
    private String Audio_Post1="POST /audio.input HTTP/1.1\r\nHost: ";
    private String Audio_Post2="\r\nContent-Type: audio/wav\r\nContent-Length: ";
    private String Audio_Post3="\r\nAccept: */*\r\n\r\n";
    public void sendAudio(String ip,int port,byte[] buf,int len)
    {
        if(audioSocket==null)
        {
            audioSocket=new TcpSocket();
            int send_len=1024;
            int cur_len=0;
            int send_time=1;
            if(audioSocket.Connect(ip, port))
            {
                String Audio_Str=Audio_Post1+ip+Audio_Post2+len+Audio_Post3;
                audioSocket.Send_Str(Audio_Str);//Send HTTP Header
                try
                {
                    Thread.sleep(100);
                } catch (InterruptedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                //Send data 2s, then wait 2s, make sure module receive completely
                while(cur_len<len)
                {
                    if(len-cur_len>send_len)
                    {
                        audioSocket.Send_Byte(buf, cur_len, send_len);
                        cur_len+=send_len;
                        Log.i("cur_len==>", cur_len+"");
                    }
                    else
                    {
                        audioSocket.Send_Byte(buf, cur_len, len-cur_len);
                        cur_len=len;
                        Log.i("cur_len==>", cur_len+"");
                        break;
                    }
                    if(cur_len>=32000*send_time){
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        send_time++;
                    }

                }
            }
        }
        closeSocket();
    }

    /**
     * Description: Close socket
     */
    public void closeSocket() {
        if(audioSocket!=null)
        {
            audioSocket.Close();
            audioSocket=null;
        }
    }
}
