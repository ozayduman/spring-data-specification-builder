package com.github.ozayduman.specificationbuilder;

import com.github.ozayduman.specificationbuilder.entity.Employee;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@UtilityClass
public class TestDataGenerator {

    public Employee create(String name, String surname, String email, String birdDate){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/d");
        return new Employee(name,surname,email, LocalDate.parse(birdDate,formatter));
    }

    public List<Employee> createEmployees(){
        return List.of(
                create("Doloritas", "Yewdell", "dyewdell3@earthlink.net", "1991/06/11"),
                create("April", "Cargill", "acargill4@i2i.jp", "2020/08/10"),
                create("Ermina", "Chisnell", "echisnell5@ycombinator.com", "2007/08/13"),
                create("Dominik", "Gyngyll", "dgyngyll6@wix.com", "1991/08/23"),
                create("Marcy", "Schwaiger", "mschwaiger7@diigo.com", "1994/04/14"),
                create("Abbey", "Muddicliffe", "amuddicliffe8@wufoo.com", "2005/06/19"),
                create("Ebony", "Richardeau", "erichardeau9@bluehost.com", "2004/11/15"),
                create("Falkner", "McBay", "fmcbaya@mozilla.org", "1986/03/30"),
                create("Farra", "Tatnell", "ftatnellb@symantec.com", "2001/02/24"),
                create("Filippa", "Willows", "fwillowsc@sbwire.com", "1988/09/20"),
                create("Marietta", "Trowsdale", "mtrowsdaled@mayoclinic.com", "2004/05/04"),
                create("Iggy", "Yanson", "iyansone@forbes.com", "1987/03/23"),
                create("Margy", "Bechley", "mbechleyf@nifty.com", "1986/05/26"),
                create("Norman", "Gresch", "ngreschg@parallels.com", "2008/10/19"),
                create("Tamiko", "MacManus", "tmacmanush@ask.com", "1993/09/15"),
                create("Hillie", "Conklin", "hconklini@google.com.br", "2013/02/18"),
                create("Loren", "Neaverson", "lneaversonj@woothemes.com", "1994/05/05"),
                create("Cammie", "Perago", "cperagok@gravatar.com", "2012/04/07"),
                create("Salim", "Ganing", "sganingl@infoseek.co.jp", "1998/03/17"),
                create("Kass", "Coltherd", "kcoltherdm@techcrunch.com", "1996/11/26"),
                create("Carla", "Lutas", "clutasn@usnews.com", "2001/10/03"),
                create("Standford", "Badwick", "sbadwicko@rediff.com", "2011/01/07"),
                create("Lorne", "Mewis", "lmewisp@alexa.com", "1993/10/15"),
                create("Ric", "Quaife", "rquaifeq@dot.gov", "2019/05/15"),
                create("Paulina", "Benjafield", "pbenjafieldr@wiley.com", "2006/12/04"),
                create("Grant", "Bahl", "gbahls@hatena.ne.jp", "2001/12/09"),
                create("Julieta", "Greenroyd", "jgreenroydt@deviantart.com", "1997/03/11"),
                create("Freemon", "Roth", "frothu@mayoclinic.com", "2013/06/10"),
                create("Alice", "Bentz", "abentzv@icq.com", "2008/08/05"),
                create("Ruthanne", "Haking", "rhakingw@telegraph.co.uk", "2011/03/22"),
                create("Cedric", "Antoniutti", "cantoniuttix@nasa.gov", "2002/01/26"),
                create("Sydney", "Maddison", "smaddisony@bloomberg.com", "1999/10/10"),
                create("Elsy", "McClymont", "emcclymontz@google.it", "2005/08/30"),
                create("Marshal", "Ripping", "mripping10@wsj.com", "1992/07/25"),
                create("Luce", "Sparrow", "lsparrow11@usa.gov", "1990/07/25"),
                create("Blinny", "Lusk", "blusk12@squidoo.com", "1994/10/07")
        );
    }
}
