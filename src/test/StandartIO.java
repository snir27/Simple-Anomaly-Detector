package test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import test.Commands.DefaultIO;

public class StandartIO implements DefaultIO {

	Scanner in;
	PrintWriter out;
	public StandartIO() {
		try {
			in=new Scanner(System.in);
			out=new PrintWriter(System.out);			
		} catch (Exception e) {}
	}
	
	@Override
	public String readText() {
		return in.nextLine();
	}

	@Override
	public void write(String text) {
		out.print(text);
	}

	@Override
	public float readVal() {
		return in.nextFloat();
	}

	@Override
	public void write(float val) {
		out.print(val);
		out.flush();
	}

	public void close() {
		in.close();
		out.close();
	}
}