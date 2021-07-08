package com.lhz;

import com.google.protobuf.InvalidProtocolBufferException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author lhzlhz
 * @create 2021/6/22
 */
public class MainTest {

	public static void main(String[] args) throws InvalidProtocolBufferException {
		byte[] rse = send(new ArrayList<String>(Arrays.asList("1xxxx", "2xxxx", "33xxx")));
		DataInfo.IPAck ipAck = DataInfo.IPAck.parseFrom(rse);

		System.out.println(ipAck.getResCount());
		for (int i = 0; i < ipAck.getResCount(); i++) {
			System.out.println(ipAck.getRes(i).getIp());
			System.out.println(ipAck.getRes(i).getMac());
		}

	}

	public static byte[] send(List<String> ips) throws InvalidProtocolBufferException {
		DataInfo.IPs.Builder builder = DataInfo.IPs.newBuilder();
		for (int i = 0; i < ips.size(); i++) {
			builder.addIp(ips.get(i));

		}
		builder.setSource("ip!!!!");
		DataInfo.IPs ipsParam = builder.build();

		return getRes(ipsParam);

	}

	public static byte[] getRes(DataInfo.IPs ips) throws InvalidProtocolBufferException {
		byte[] ipsBattery = ips.toByteArray();
		DataInfo.IPs iPsParam = DataInfo.IPs.parseFrom(ipsBattery);
		List<String> ipstring = new ArrayList<String>();
		for (int i = 0; i < iPsParam.getIpCount(); i++) {
			ipstring.add(iPsParam.getIp(i));
		}
		System.out.println("recv from param  source"+iPsParam.getSource());
		System.out.println("recv from param  ip" + ipstring.toString());

		DataInfo.IPAck.Builder ackB = DataInfo.IPAck.newBuilder();

		for (int i = 0; i < iPsParam.getIpCount() * 4; i++) {
			DataInfo.IPAck.ipRes.Builder resBuilder = DataInfo.IPAck.ipRes.newBuilder();
			resBuilder.setIp("ip  11111111"+i+"  "+iPsParam.getIp(0));
			resBuilder.setMac("mac 11111111"+i+"  "+iPsParam.getIp(0));
			DataInfo.IPAck.ipRes res = resBuilder.build();
			ackB.addRes(res);
		}

		DataInfo.IPAck ipAck = ackB.build();


		return ipAck.toByteArray();


	}

}
