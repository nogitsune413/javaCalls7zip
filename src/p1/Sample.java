package p1;

import static java.lang.System.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class Sample
{
    /** エントリポイント（テスト用）
     *
     * @param args 未使用
     */
    public static void main(String[] args)
    {
        final Path targetZipFile = Paths.get("C:\\temp\\zip\\sample.zip");        // 解凍する圧縮ファイル
        final Path unZipFolder   = Paths.get("C:\\temp\\unZip");                  // 解凍先フォルダ
        final Path sevenZipTool  = Paths.get("C:\\Program Files\\7-Zip\\7z.exe"); // 7-zipのコマンドラインツール
        final Path sevenZipLog   = Paths.get("C:\\temp\\log\\7zipStdout.log");    // 7-zipの実行時ログ

        // 圧縮コマンドを取得する。
        List<String> unZipCmd = getUnZipCommand(targetZipFile,unZipFolder,sevenZipTool);

        // 外部コマンドを実行する。
        runExternalCommand(unZipCmd,sevenZipLog);
    }

    /** 外部コマンドを呼び出す。
     *
     * @param command 呼び出すコマンド及びコマンド引数が格納されたリスト
     * @param log     コマンド実行時の標準出力及び標準エラー出力を書き出すログファイル
     */
    private static void runExternalCommand(List<String> command,Path log)
    {
        ProcessBuilder pb = new ProcessBuilder(command); // コマンド実行用のプロセスを準備する。
        pb.redirectErrorStream(true);                    // 標準出力と標準エラー出力の出力先を同じにする。
        pb.redirectOutput(log.toFile());                  // 標準出力と標準エラー出力の出力先ファイルを設定。

        try
        {
            Process proc = pb.start();     // コマンドを実行
            int exitCode = proc.waitFor(); // コマンドの終了を待機する。

            out.println(getResultMessage(command,exitCode)); // コマンドの実行結果を表示
        }
        catch(IOException | InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    /** 解凍するための外部コマンドを取得する。<br>
     *  解凍に使用するのは、7-zipのコマンドラインツール「7z.exe」である。
     *
     * @param targetZipFile 解凍する圧縮ファイル
     * @param dstFolder     解凍先フォルダ
     * @param sevenZipExe   コマンドラインツール「7z.exe」
     * @return              解凍コマンドリスト
     */
    private static List<String> getUnZipCommand(Path targetZipFile,Path dstFolder,Path sevenZipExe)
    {
        final String EXTRACT = "x";  // パラメータ：圧縮ファイル内のフォルダ構造を保持したまま展開する。
        final String OUTPUT  = "-o"; // パラメータ：出力先フォルダを指定する。

        List<String> cmd = new ArrayList<>();

        cmd.add(sevenZipExe.toString());             // 7-zipのコマンドラインツール
        cmd.add(EXTRACT);                            // 展開する。
        cmd.add(OUTPUT + dstFolder);                 // 解凍先フォルダの指定
        cmd.add(targetZipFile.toString());           // 解凍する圧縮ファイル

        return cmd;
    }

    /** コマンドの実行結果を表す文字列を取得する。
     *
     * @param command 実行したコマンドを表す配列
     * @param exitCode 実行コマンドの終了コード
     * @return コマンドの実行結果を表す文字列
     */
    private static String getResultMessage(List<String> command, int exitCode)
    {
        final String SPACE = " "; // コマンドリストを区切る空白文字

        return MessageFormat.format("外部コマンド「{0}」を実行しました。終了コード:{1}",
        		                      String.join(SPACE,command),
        		                      exitCode);
    }
}