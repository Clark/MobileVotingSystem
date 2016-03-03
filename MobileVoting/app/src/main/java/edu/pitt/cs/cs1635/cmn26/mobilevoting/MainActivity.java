package edu.pitt.cs.cs1635.cmn26.mobilevoting;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static MainActivity inst;
    private String admin = "";

    List<String> participants;
    HashMap<String, String> tallyTable;
    String currentLogs;
    int[] votes;

    TextView logs;


    public static MainActivity instance() {
        return inst;
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);

        logs = (TextView) findViewById(R.id.logs);
        currentLogs = logs.getText().toString();
    }

    public void parseSMS(String sender, String body) {
        if(body.toLowerCase().equals("init") && admin.equals(getString(R.string.emptyString))) {
            createVotingComponent(sender);
            return;
        }
        logs.setText(currentLogs);
        // Admin Functions
        //if(sender.equals(admin)) {
            //String[] bodySplit = body.split(getString(R.string.space));
            if(body.toLowerCase().equals("start")) initializeTallyTable(sender);
            else if(body.toLowerCase().equals("end")) endVoting(sender);
            else if(body.split(getString(R.string.space))[0].toLowerCase().equals("add")) addParticipants(sender, body);
        //} else {
            // Voters
            else {
            handleVote(sender, body);
        }
    }

    private void acknowledge(String Receiver, String ack) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(Receiver, null, ack, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void endVoting(String sender) {
        Iterator iterator = tallyTable.values().iterator();
        votes = new int[participants.size()];
        while(iterator.hasNext()) {
            int vote = Integer.parseInt((String)iterator.next());
            vote--; // ArrayList size() to array
            votes[vote]++;
        }

        // Find max number of votes
        int max = 0;
        for(int i = 0; i < votes.length; i++) {
            if(votes[i] > max) max = votes[i];
        }
        // Get and print the winners
        String winners = "";
        for(int i = 0; i < votes.length; i++) {
            if(votes[i] == max) {
                int adjusted = i + 1;
                winners = winners + "  " + adjusted;
            }
        }
        winners = "\nThe winners are: " + winners + " with " + max + " votes!";
        currentLogs = currentLogs + winners;
        logs.setText(currentLogs);
        acknowledge(sender, winners);


    }

    private void addParticipants(String sender, String body) {
        // Participants will be given in a single string, separated by commas
        body = body.substring(4);
        String[] newParticipants = body.split(",");

        // If the participant does not already exist, enter it into the list
        for(int i = 0; i < newParticipants.length; i++) {
            if(!participants.contains(newParticipants[i])) {
                participants.add(newParticipants[i]);
                currentLogs = currentLogs + "\n" + newParticipants[i] + " was added with the participant number of " + participants.size();
                logs.setText(currentLogs);
            }
        }
        acknowledge(sender, "All participants successfully added.");
    }

    private void handleVote(String sender, String body) {
        // Returns error, shouldn't be more than just the vote
        //if(body.split(getString(R.string.space)).length > 1) {
            // Return only one arg should be taken
        //} else {
            // If the participant exists
            //if(participants.size() < Integer.parseInt(body)) {
            if(Integer.parseInt(body) <= participants.size() && Integer.parseInt(body) > 0) {
                if(tallyTable.containsKey(sender)) {
                    // Handle user has already voted
                    currentLogs = currentLogs + "\n " + sender + " has already voted. Vote ineffective.";
                    logs.setText(currentLogs);
                    acknowledge(sender, "You have already voted.");
                } else {
                    tallyTable.put(sender, body);
                    currentLogs = currentLogs + "\n " + sender + " has voted for " + body;
                    logs.setText(currentLogs);
                    acknowledge(sender, "Vote successful.");
                }
            } else {
                currentLogs = currentLogs + "\n This participant does not exist.";
                logs.setText(currentLogs);
                acknowledge(sender, "This participant does not exist");
            }
        //}

    }

    private void initializeTallyTable(String sender) {
        tallyTable = new HashMap<String, String>();
        currentLogs = currentLogs + getString(R.string.newLine) +  getString(R.string.ackTallyInit);
        logs.setText(currentLogs);

        acknowledge(sender, getString(R.string.ackTallyInit));
    }

    private void createVotingComponent(String sender) {
        admin = sender; // The user who creates component is the admin
        participants = new ArrayList<String>();
        // Send a SMS back saying "You are now the admin of the Mobile Voting System"

        currentLogs = currentLogs + "\n" + admin + " is now the admin of the Mobile Voting System";
        logs.setText(currentLogs);

        acknowledge(sender, getString(R.string.ackCreateVoting));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
