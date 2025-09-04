package consoleokr;
import java.util.*;

class Employee {
    private String empId, name, role, dept, reviewer;
    protected double rating;

    public Employee(String empId, String name, String role, String dept, String reviewer) {
        this.empId = empId; this.name = name; this.role = role; this.dept = dept; this.reviewer = reviewer;
    }
    public String getEmpId() { return empId; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getDept() { return dept; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public double finalRating() { return rating; }
}

class Manager extends Employee {
    public Manager(String empId, String name, String role, String dept, String reviewer) {
        super(empId, name, role, dept, reviewer);
    }
    @Override
    public double finalRating() { return getRating() * 1.1; }
}

class Objective {
    private String objId, title, description, status;
    private double weightage;
    public Objective(String objId, String title, String description, double weightage, String status) {
        this.objId = objId; this.title = title; this.description = description;
        this.weightage = weightage; this.status = status;
    }
    public String getObjId() { return objId; }
    public String getTitle() { return title; }
    public double getWeightage() { return weightage; }
    public String getStatus() { return status; }
}

class KeyResult {
    private String krId, objectiveId, metric;
    private double target, progress;
    public KeyResult(String krId, String objectiveId, String metric, double target, double progress) {
        this.krId = krId; this.objectiveId = objectiveId; this.metric = metric;
        this.target = target; this.progress = progress;
    }
    public String getKrId() { return krId; }
    public String getObjectiveId() { return objectiveId; }
    public String getMetric() { return metric; }
    public double getTarget() { return target; }
    public double getProgress() { return progress; }
    public void setProgress(double progress) { this.progress = progress; }
}

class ReviewService {
    private Map<String, List<Objective>> empObjectives = new HashMap<>();
    private Map<String, List<KeyResult>> objKeyResults = new HashMap<>();

    public void addObjective(Employee emp, Objective obj) {
        empObjectives.computeIfAbsent(emp.getEmpId(), k -> new ArrayList<>()).add(obj);
    }
    public void addKeyResult(Objective obj, KeyResult kr) {
        objKeyResults.computeIfAbsent(obj.getObjId(), k -> new ArrayList<>()).add(kr);
    }
    public void updateProgress(String krId, double value) {
        for (List<KeyResult> list : objKeyResults.values())
            for (KeyResult kr : list)
                if (kr.getKrId().equals(krId)) kr.setProgress(value);
    }
    public void consolidateRating(Employee emp) {
        List<Objective> objs = empObjectives.getOrDefault(emp.getEmpId(), new ArrayList<>());
        double total = 0;
        for (Objective obj : objs) {
            List<KeyResult> krs = objKeyResults.getOrDefault(obj.getObjId(), new ArrayList<>());
            double objProgress = 0;
            for (KeyResult kr : krs) objProgress += (kr.getProgress() / kr.getTarget()) * 100;
            if (!krs.isEmpty()) objProgress /= krs.size();
            total += objProgress * obj.getWeightage();
        }
        emp.setRating(total / 100);
    }
    public void printScorecard(Employee emp) {
        System.out.println("\nScorecard for " + emp.getName() + " (" + emp.getRole() + ")");
        List<Objective> objs = empObjectives.getOrDefault(emp.getEmpId(), new ArrayList<>());
        for (Objective obj : objs) {
            System.out.println("Objective: " + obj.getTitle() + " [" + obj.getStatus() + "]");
            List<KeyResult> krs = objKeyResults.getOrDefault(obj.getObjId(), new ArrayList<>());
            for (KeyResult kr : krs) {
                System.out.println("  KR: " + kr.getMetric() + " Progress: " + kr.getProgress() + "/" + kr.getTarget());
            }
        }
        System.out.println("Final Rating: " + emp.finalRating());
    }
}

public class OKRAppMain {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ReviewService service = new ReviewService();

        System.out.print("Enter Employee ID: ");
        String eid = sc.nextLine();
        System.out.print("Enter Employee Name: ");
        String ename = sc.nextLine();
        System.out.print("Enter Role (Developer/Manager): ");
        String erole = sc.nextLine();
        System.out.print("Enter Department: ");
        String edept = sc.nextLine();
        System.out.print("Enter Reviewer ID: ");
        String reviewer = sc.nextLine();

        Employee emp = erole.equalsIgnoreCase("Manager")
                ? new Manager(eid, ename, erole, edept, reviewer)
                : new Employee(eid, ename, erole, edept, reviewer);

        System.out.print("How many objectives to add? ");
        int nobj = sc.nextInt(); sc.nextLine();

        for (int i = 1; i <= nobj; i++) {
            System.out.println("\nEnter details for Objective " + i);
            System.out.print("Objective ID: "); String oid = sc.nextLine();
            System.out.print("Title: "); String title = sc.nextLine();
            System.out.print("Description: "); String desc = sc.nextLine();
            System.out.print("Weightage (0-1): "); double wt = sc.nextDouble(); sc.nextLine();
            System.out.print("Status (Open/Closed): "); String status = sc.nextLine();

            Objective obj = new Objective(oid, title, desc, wt, status);
            service.addObjective(emp, obj);

            System.out.print("How many Key Results for this Objective? ");
            int nkr = sc.nextInt(); sc.nextLine();

            for (int j = 1; j <= nkr; j++) {
                System.out.println("  Enter details for Key Result " + j);
                System.out.print("  KR ID: "); String krid = sc.nextLine();
                System.out.print("  Metric: "); String metric = sc.nextLine();
                System.out.print("  Target value: "); double target = sc.nextDouble();
                System.out.print("  Progress value: "); double progress = sc.nextDouble(); sc.nextLine();
                KeyResult kr = new KeyResult(krid, oid, metric, target, progress);
                service.addKeyResult(obj, kr);
            }
        }

        service.consolidateRating(emp);
        service.printScorecard(emp);
        sc.close();
    }
}
