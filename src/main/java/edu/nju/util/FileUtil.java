package edu.nju.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

	public static String uniformPathSeparator(String path) {
		String result = path;
		String[] separators = { "\\", "//", "/" };
		for (String separator : separators) {
			if (!File.separator.equals(separator)) {
				result = result.replace(separator, File.separator);
			}
		}
		return result;
	}

	public static String jointPath(String... parts) {
		String path = "";
		for (String part : parts) {
			String toJoint = uniformPathSeparator(part);
			if (toJoint.startsWith(File.separator)) {
				toJoint = toJoint.substring(1, toJoint.length());
			}
			if (!path.endsWith(File.separator) && !path.trim().equals("")) {
				path += File.separator;
			}
			path = path + toJoint;
		}
		return path;
	}

	public static String read(String filePath) {
		File file = new File(filePath);
		return read(file);
	}

	public static String read(String filePath, String encoding) {
		File file = new File(filePath);
		return read(file, encoding);
	}

	public static String read(File file) {
		return read(file, "utf-8");
	}

	public static String read(File file, String encoding) {
		String content = "";
		BufferedReader bufferedReader = null;
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), encoding);
			bufferedReader = new BufferedReader(inputStreamReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				content += line;
				content += System.getProperty("line.separator");
			}
		} catch (Exception e) {
		} finally {
			if (bufferedReader != null)
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return content;
	}

	public static List<File> findJavaFiles(String srcDirPath) {
		List<File> result = new ArrayList<>();

		findJavaFilesInDir(srcDirPath, result);

		return result;

	}

	private static void findJavaFilesInDir(String srcDirPath, List<File> result) {
		File file = new File(srcDirPath);
		if (file.exists()) {
			File[] files = file.listFiles();
			for (File file2 : files) {
				if (file2.isDirectory()) {
					findJavaFilesInDir(file2.getAbsolutePath(), result);
				} else {
					String fileName = file2.getName();
					if (fileName.endsWith(".java")) {
						result.add(file2);
					}
				}
			}
		}
	}
}
