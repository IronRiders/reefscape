import java.beans.Transient;

import org.ironriders.drive.DriveSubsystem;
import org.ironriders.elevator.ElevatorCommands;
import org.ironriders.elevator.ElevatorSubsystem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class test {
    DriveSubsystem driveSubsystem=new DriveSubsystem();
    ElevatorSubsystem elevatorSubsystem=new ElevatorSubsystem();
    ElevatorCommands elevatorCommands =elevatorSubsystem.getCommands();
    @Test
    void test1(){
        assert(1==1);
    }
}
