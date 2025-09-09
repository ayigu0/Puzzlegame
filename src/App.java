import ui.LoginJFrame;

/// 点开puzzlegame找到pictures文件夹，右击pictures文件夹，点copy path/reference
/// 然后点absolute path，把absolute path 复制给下面的变量 main_path，运行App文件就可以开始玩了！
/// 记得要把main_path变成string喔！
/// 这个操作只在mac系统上有用，如果是windows系统图片的路径不一样的.
public class App {
    private static final String main_path = "/Users/peeko/IdeaProjects/puzzlegame/pictures";


    public static void main(String[] args) {

        new LoginJFrame(main_path + "/");

    }

}
