package logbook.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class MakeShortCut {

	public static void main(String[] args) {
		MakeShortCut.creatShotcut("shortcutmaker.js", new File("MONOMONO.EXE"), "", "MONOMONO");
	}

	public static void creatShotcut(String temp, File source, String folder, String name) {
		try (PrintStream out = new PrintStream(new FileOutputStream(new File(temp)))) {
			out.println("WScript.Echo(\"Creating   shortcuts...\");");
			out.println("Shell   =   new   ActiveXObject(\"WScript.Shell\");");
			out.println("ProgramsPath   =   Shell.SpecialFolders(\"Programs\");");

			/** 创建菜单快捷方式 */
			out.println("fso   =   new   ActiveXObject(\"Scripting.FileSystemObject\");");
			out.println("if   (!fso.folderExists(ProgramsPath   +   \"\\\\" + folder + "\"))");
			out.println("fso.CreateFolder(ProgramsPath   +   \"\\\\" + folder + "\");");
			out.println("link   =   Shell.CreateShortcut(ProgramsPath   +   \"\\\\" + folder + "\\\\" + name + ".lnk\");");
			out.println("link.Arguments   =   \"\";");
			out.println("link.Description   =   \"" + name + "\";");
			out.println("link.HotKey   =   \"\";");
			out.println("link.IconLocation   =   \"" + source.getAbsolutePath().replace("\\", "\\\\") + ",0\";");
			out.println("link.TargetPath   =   \"" + source.getAbsolutePath().replace("\\", "\\\\") + "\";");
			out.println("link.WindowStyle   =   1;");
			out.println("link.WorkingDirectory   =   \"" + source.getAbsolutePath().replace("\\", "\\\\") + "\";");
			out.println("link.Save();");

			/** 创建桌面快捷方式 */
			out.println("DesktopPath   =   Shell.SpecialFolders(\"Desktop\");");
			out.println("link   =   Shell.CreateShortcut(DesktopPath   +   \"\\\\" + name + ".lnk\");");
			out.println("link.Arguments   =   \"\";");
			out.println("link.Description   =   \"" + name + "\";");
			out.println("link.HotKey   =   \"\";");
			out.println("link.IconLocation   =   \"" + source.getAbsolutePath().replace("\\", "\\\\") + ",0\";");
			out.println("link.TargetPath   =   \"" + source.getAbsolutePath().replace("\\", "\\\\") + "\";");
			out.println("link.WindowStyle   =   1;");
			out.println("link.WorkingDirectory   =   \"" + source.getAbsoluteFile().getParentFile().getAbsolutePath().replace("\\", "\\\\") + "\";");
			out.println("link.Save();");
			out.println("WScript.Echo(\"Shortcuts   created.\");");
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			Process process = Runtime.getRuntime().exec("cmd.exe   /c   cscript.exe   /nologo    " + temp);
			process.waitFor();
			System.out.println("创建快捷方式" + (process.exitValue() == 0 ? "成功" : "失败"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		new File(temp).delete();
	}

	public static String escaped(String path) {
		return path.replace("\\", "\\\\");
	}

}
