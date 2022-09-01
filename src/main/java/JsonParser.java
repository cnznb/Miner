import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.eclipse.jgit.lib.Repository;
import org.refactoringminer.api.GitHistoryRefactoringMiner;
import org.refactoringminer.api.GitService;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringHandler;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;

public class JsonParser {
    public static void main(String args[]) throws Exception{
        String json = "C:\\Users\\winner\\Desktop\\refactorings.json";
        File jsonFile = new File(json);
        readerMethod(jsonFile);
    }
    private static void readerMethod(File file) throws IOException {
        FileReader fileReader = new FileReader(file);
        Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
        int ch = 0;
        StringBuffer sb = new StringBuffer();
        while ((ch = reader.read()) != -1) {
            sb.append((char) ch);
        }
        fileReader.close();
        reader.close();
        String jsonStr = sb.toString();
        GitService gitService = new GitServiceImpl();
        GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();
        int i=0;
        for (Object o : JSON.parseArray(jsonStr)) {
            commit c = JSON.parseObject(o.toString(),commit.class);
            List<refactoring> lists = c.getRefactorings();
            String names = c.getRepository().split("/")[4];
            String nos = names.split("\\.")[0];
            int flag = 0;
            for (refactoring list : lists) {
                if(list.getType().equals("Extract Method")){
                    flag = 1;
                    break;
                }
            }
            if(flag==0)continue;
            i++;
            System.out.println(c.getSha1()+' '+nos);
            /*if(lists.get(0).getType().equals("Extract Method")){
                continue;
                //System.out.println(c.getSha1()+' '+c.getRepository());

            }
            else {
                int flag = 0;
                for (refactoring list : lists) {
                    if(list.getType().equals("Extract Method")){
                        flag = 1;
                        break;
                    }
                }
                if(flag==0)continue;
                System.out.println(c.getSha1()+' '+c.getRepository());
                String path = "D:\\dataset\\"+c.getSha1();
                File f = new File(path+"\\"+c.getSha1()+".json");
                File ff = new File(path);
                //Files.copy()
                Writer writer = new OutputStreamWriter(new FileOutputStream(f),StandardCharsets.UTF_8);
                try {
                    String name = c.getRepository().split("/")[4];
                    String no = name.split("\\.")[0];
                    //System.out.println(no);
                    //if(!no.equals("spring-data-rest"))continue;
                    Repository repo = gitService.cloneIfNotExists(
                            "tmp/"+no,
                            c.getRepository());
                    miner.detectAtCommit(repo, c.getSha1(), new RefactoringHandler() {
                        @Override
                        public void handle(String commitId, List<Refactoring> refactorings) {
                            for (Refactoring ref : refactorings) {
                                if(ref.getName().equals("Extract Method")){
                                    try {
                                        writer.write(ref.toJSON());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        writer.flush();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                writer.close();
            }*/
        }
        System.out.println(i);
    }
}
