/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blacksheep.ide;
import java.io.*;
import javax.swing.JFileChooser;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
/*
 *
 * @author Dantin
 */
public class CurrentWorkspace 
{
    String code;
    File currentFile;
    List errorList;
    public CurrentWorkspace()
    {
        currentFile = null;
        code = null;
        errorList = new ArrayList();
    }
    public void saveAs(String fileName,String codeblocks) throws FileNotFoundException,IOException
    {
        currentFile = new File(fileName+".java");
        code = codeblocks;
        if(currentFile.exists())
            currentFile.delete();
        currentFile.createNewFile();
        PrintWriter printWriter = new PrintWriter(currentFile);
        printWriter.print(code);
        printWriter.close();
    }
    public void save(String fileName,String codeblocks) throws FileNotFoundException,IOException
    {
        // TODO JFileChooser Implementation from wherever save is called if called for the first time
        // TODO save as logic
        if(currentFile == null)
        {
            currentFile = new File(fileName+".java");
            code = codeblocks;
            if(currentFile.exists())
            {
                PrintWriter printWriter = new PrintWriter(currentFile);
                printWriter.print(code);
                printWriter.close();
            }
            else
            {
                currentFile.createNewFile();
                PrintWriter printWriter = new PrintWriter(currentFile);
                printWriter.print(code);
                printWriter.close();
            }
        }
        else
        {
            currentFile.delete();
            currentFile.createNewFile();
            System.out.println("we are here");
            PrintWriter printWriter = new PrintWriter(currentFile);
            code = codeblocks;
            printWriter.print(code);
            printWriter.close();
        }
    }
    public static int getLineStart(int ln,String cd)
    {
        char b[] = cd.toCharArray();
        int i;
        for(i=0;ln>1;i++)
        {
            if(b[i]=='\n')
                --ln;
        }
        return i;
    }
    @SuppressWarnings("empty-statement")
    public static int getLineEnd(int ln,String cd)
    {
        char b[] = cd.toCharArray();
        int i=getLineStart(ln,cd);
        for(;i<cd.length()&&b[i]!='\n';i++);
        return i;
    }
    public String open() throws IOException 
    {
        JFileChooser g = new JFileChooser();
        g.setApproveButtonText("Open");
        g.setFileSelectionMode(JFileChooser.FILES_ONLY);
        g.setDialogType(JFileChooser.OPEN_DIALOG);
        g.showDialog(g,"Open File");
        currentFile = g.getSelectedFile();
        code = "";
        FileInputStream fin = new FileInputStream(currentFile);
        BufferedReader br = new BufferedReader(new InputStreamReader(fin));
        String x;
        while((x=br.readLine())!=null)
        {
            code+=(x+"\n");
        }
        return code;
    }
    public static int saveConfirmation()
    {
        Object options[] = {
                    "Save",
                    "No",
                    "Cancel"
                };
        return JOptionPane.showOptionDialog(null,
                "Would you like to save your unsaved changes?",
                "Unsaved Changes",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,    
                options, 
                options[0]); 
    }
    public static String chooseFile()
    {
        JFileChooser jfc = new JFileChooser();
        jfc.setApproveButtonText("Save");
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jfc.setDialogType(JFileChooser.SAVE_DIALOG);
        jfc.showDialog(jfc,"Save");
        String name = jfc.getSelectedFile().getName();
        String dir = jfc.getCurrentDirectory().getAbsolutePath();
        return dir+"\\"+name;
    }
    public String build(String codeswag) throws IOException,NumberFormatException
    {       int length;
        if(!errorList.isEmpty())
            errorList.removeAll(errorList);
        if(currentFile!=null)
        {length = this.currentFile.getAbsolutePath().length()+1;}
        if(isSaved(codeswag) && currentFile.exists())    
        {
            System.err.println("Using 1st option");
            Process pc = Runtime.getRuntime().exec("javac \""+currentFile.getAbsolutePath()+"\"");
            String x = "";
            BufferedReader br = new BufferedReader(new InputStreamReader(pc.getErrorStream()));
            String nl;
            while((nl = br.readLine())!=null)
            {
                x=x+nl;
            }
            int m;
            int index = 0;
            while((m = x.indexOf(currentFile.getAbsolutePath(), index))!=-1)
            {
                //find the number
                m += currentFile.getAbsolutePath().length()+1;
                String num="";
                while(x.charAt(m)!=':')
                {
                    num+=x.charAt(m);
                    m++;
                }
                errorList.add(Integer.parseInt(num.trim()));
                //increment index
                index++;
            }
            if(x.equals(""))
            {
                return "Program successfully compiled, returned no errors.";
            }
            else {
                return x;
            }
        }
        else if(currentFile == null)
        {
            System.err.println("Using 2nd option");
            JFileChooser jfc = new JFileChooser();
            jfc.setApproveButtonText("Save");
            jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            jfc.setDialogType(JFileChooser.SAVE_DIALOG);
            int showDialog = jfc.showDialog(jfc,"Save");
            String name = jfc.getSelectedFile().getName();
            String dir = jfc.getCurrentDirectory().getAbsolutePath();
            save(dir+"\\"+name, codeswag);
            System.out.println(currentFile.getAbsolutePath());
            Process pc = Runtime.getRuntime().exec("javac \""+currentFile.getAbsolutePath()+"\"");
            String x = "";
            BufferedReader br = new BufferedReader(new InputStreamReader(pc.getErrorStream()));
            String nl;
            while((nl = br.readLine())!=null)
            {
                x=x+nl;           
            }
            int m;
            int index = 0;
            while((m = x.indexOf(currentFile.getAbsolutePath(), index))!=-1)
            {
                //find the number
                m += currentFile.getAbsolutePath().length()+1;
                String num="";
                while(x.charAt(m)!=':')
                {
                    num+=x.charAt(m);
                    m++;
                }
                errorList.add(Integer.parseInt(num.trim()));
                //increment index
                index++;
            }
            if(x.equals(""))
            {
                return "Program successfully compiled, returned no errors.";
            }
            else {
                return x;
            }
        }
        else
        {
            System.err.println("Using 3rd option");
            currentFile.delete();
            currentFile.createNewFile();
            PrintWriter printWriter = new PrintWriter(currentFile);
            code = codeswag;
            printWriter.print(code);
            printWriter.close();
            //Process pc = new ProcessBuilder("C:\\Program Files\\Java\\jdk1.8.0_91\\bin\\javac.exe",currentFile.getAbsolutePath()).start();
            Process pc = Runtime.getRuntime().exec("javac \""+currentFile.getAbsolutePath()+"\"");
            String x = "";
            BufferedReader br = new BufferedReader(new InputStreamReader(pc.getErrorStream()));
            String nl;
            while((nl = br.readLine())!=null)
            {
                x=x+nl;
            }
            int m;
            int index = 0;
            while((m = x.indexOf(currentFile.getAbsolutePath(), index))!=-1)
            {
                //find the number
                m += currentFile.getAbsolutePath().length()+1;
                String num="";
                while(x.charAt(m)!=':')
                {
                    num+=x.charAt(m);
                    m++;
                }
                errorList.add(Integer.parseInt(num.trim()));
                //increment index
                index++;
            }
            if(x.equals(""))
            {
                return "Program successfully compiled, returned no errors.";
            }
            else {
                return x;
            }
        }
    }
    public void run() throws IOException, InterruptedException, URISyntaxException
    {
        if(currentFile.exists())
        {
             System.out.println(CurrentWorkspace.getNameWithoutExtension(currentFile.getName()) + "\n" + currentFile.getParent());
             System.out.println(currentFile.getParent()+"\\run_"+CurrentWorkspace.getNameWithoutExtension(currentFile.getName())+".bat");
             File fs = new File(currentFile.getParent()+"\\"+currentFile.getName()+".bat");
             fs.createNewFile();
             PrintWriter pw = new PrintWriter(fs);
             String idg = "java -cp \"" + currentFile.getParent() + "\" " + CurrentWorkspace.getNameWithoutExtension(currentFile.getName())+"\npause";
             System.out.println(CurrentWorkspace.getNameWithoutExtension(currentFile.getName()));
             pw.print(idg);
             pw.close();
             String sc = currentFile.getParent()+"\\"+currentFile.getName()+".bat";
             System.out.println(sc);
             List commands = new ArrayList<String>();
             commands.add("cmd");
             commands.add("/c");
             commands.add("start");
             commands.add(currentFile.getName()+".bat");
             ProcessBuilder pb = new ProcessBuilder(commands);
             pb.directory(new File(currentFile.getParent()));
             pb.start();
        }
    }
    @SuppressWarnings("empty-statement")
    public static String getNameWithoutExtension(String name)
    {
        char x[] = name.toCharArray();
        int l;
        for(l = x.length-1;x[l]!='.';l--);
        char m[] = new char[x.length];
        for(int y=0;y<l;y++)
        {
            m[y] = x[y];
        }
        return new String(m);
    }
    public boolean isSaved(String codepath)
    {
            return codepath.equals(code);
    }
    public String reset() {
        currentFile = null;
        code = null;
        return "";
    }
    public static HashMap<Integer, Character> isValid(String s) {
    HashMap<Character, Character> map;
       map = new HashMap<>();
    map.put('(', ')');
    map.put('[', ']');
    map.put('{', '}');
 HashMap<Integer,Character> result;
 result=new HashMap<>();
    Stack<Character> stack = new Stack<>();
for (int i = 0; i < s.length(); i++) {
        char curr = s.charAt(i);
            if (map.keySet().contains(curr)) {
            stack.push(curr);
                        result.put(i,curr);
        } else if (map.values().contains(curr)) {
            result.put(i, curr);
                    if (!stack.empty() && map.get(stack.peek()) == curr) {
                        result.remove(i);
                        stack.pop();
                            char temp;
                            if(curr=='}')
                               temp='{';
                            else
                                if(curr==']')
                                 temp='[';
                            else
                                  temp='(';
          for (Map.Entry<Integer, Character> entry : result.entrySet()) {
     if(temp==entry.getValue()){

         result.remove(entry.getKey());
         break;
      }
}
            } 
        }
    }
 


 

return result;

   }
    public static List parenthesesCheck(String g)
    {
        List unmatched = new ArrayList<>();
        int h = 0;
        while(h<g.length())
        {
            char x = g.charAt(h);
            if(x == '[' || x == '{' || x == '(')
            {
                unmatched.add(new ParenthesisLocation(h,x));
            }
            else if(x == ']' || x == '}' || x == ')')
            {
                if(unmatched.size()>0) {
                    ParenthesisLocation pl = (ParenthesisLocation)unmatched.get(unmatched.size()-1);
                    if((pl.bracket == '[' && x == ']') || (pl.bracket=='{' && x == '}') || (pl.bracket=='(' && x==')'))
                    {
                        unmatched.remove(unmatched.size()-1);
                    }
                    else
                    {
                        unmatched.add(new ParenthesisLocation(h,x));
                    }
                }
                else
                {
                    unmatched.add(new ParenthesisLocation(h,x));
                }
            }
            ++h;
        }
        h = 0;
        int ex = unmatched.size()-1;
//        ParenthesisLocation pls = (ParenthesisLocation)unmatched.get(h);
//        ParenthesisLocation ss = (ParenthesisLocation)unmatched.get(ex);
//        System.out.println(pls.bracket+" "+pls.locale+"\n"+ss.bracket+" "+ss.locale);
        while(h<ex)
        {
            ParenthesisLocation pl = (ParenthesisLocation)unmatched.get(h);
            ParenthesisLocation s = (ParenthesisLocation)unmatched.get(ex);
            char x = s.bracket;
            if((pl.bracket == '[' && x == ']') || (pl.bracket=='{' && x == '}') || (pl.bracket=='(' && x==')'))
            {
                unmatched.remove(h);
                unmatched.remove(--ex);
            }
            ex--;
        }
        return unmatched;
    }
}