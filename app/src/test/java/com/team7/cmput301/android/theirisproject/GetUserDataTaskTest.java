/*
 * Copyright (c) Team 7, CMPUT301, University of Alberta - All Rights Reserved. You may use, distribute, or modify this code under terms and conditions of the Code of Students Behavior at University of Alberta
 */

package com.team7.cmput301.android.theirisproject;


import com.team7.cmput301.android.theirisproject.model.BodyPhoto;
import com.team7.cmput301.android.theirisproject.model.CareProvider;
import com.team7.cmput301.android.theirisproject.model.Comment;
import com.team7.cmput301.android.theirisproject.model.Patient;
import com.team7.cmput301.android.theirisproject.model.Problem;
import com.team7.cmput301.android.theirisproject.model.Record;
import com.team7.cmput301.android.theirisproject.task.Callback;
import com.team7.cmput301.android.theirisproject.task.GetUserDataTask;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetUserDataTaskTest {

    private MockPoster userPoster = new MockPoster("user");
    private MockPoster problemPoster = new MockPoster("problem");
    private MockPoster recordPoster = new MockPoster("record");
    private MockPoster commentPoster = new MockPoster("comment");
    private MockPoster bodyPhotoPoster = new MockPoster("bodyphoto");
    // TODO: add recordPhotoPoster

    @Test
    public void testGetUserData() {

        Patient patient = makePatientData();
        Patient patientConstructed = new Patient("John Wick", "jacksonheights@pm.me", "1-800-267-2001");
        uploadPatientData(patient);

        Callback cb = new Callback() {
            @Override
            public void onComplete(Object res) {
                compare(patient, patientConstructed);
            }
        };
        new GetUserDataTask(cb).execute(patientConstructed);

    }

    private void compare(Patient patient, Patient patientConstructed) {

        // compare by size of aggregate arrays because checking if every attribute of
        // every object is more work than its worth

        assertEquals(patient.getProblems().length(), patientConstructed.getProblems().length());
        assertEquals(patient.getCareProviders().size(), patientConstructed.getCareProviders().size());

        ArrayList<Integer> recordListLengths1 = new ArrayList<>();
        ArrayList<Integer> recordListLengths2 = new ArrayList<>();
        ArrayList<Integer> commentListLengths1 = new ArrayList<>();
        ArrayList<Integer> commentListLengths2 = new ArrayList<>();
        ArrayList<Integer> bpListLengths1 = new ArrayList<>();
        ArrayList<Integer> bpListLengths2 = new ArrayList<>();

        for (Problem p : patient.getProblems()) {
            recordListLengths1.add(p.getRecords().length());
            commentListLengths1.add(p.getComments().size());
            bpListLengths1.add(p.getBodyPhotos().size());
        }
        for (Problem p : patientConstructed.getProblems()) {
            recordListLengths2.add(p.getRecords().length());
            commentListLengths2.add(p.getComments().size());
            bpListLengths2.add(p.getBodyPhotos().size());
        }

        assertEquals(recordListLengths1.size(), recordListLengths2.size());
        assertEquals(commentListLengths1.size(), commentListLengths2.size());
        assertEquals(bpListLengths1.size(), bpListLengths2.size());

    }

    private void uploadPatientData(Patient user) {

        userPoster.post(user);

        for (Problem p : user.getProblems()) {
            problemPoster.post(p);
            for (Record r : p.getRecords()) {
                recordPoster.post(r);
                // TODO: post RecordPhotos
            }
            for (Comment c : p.getComments()) {
                commentPoster.post(c);
            }
            for (BodyPhoto bp : p.getBodyPhotos()) {
                bodyPhotoPoster.post(bp);
            }
        }

    }

    private Patient makePatientData(){

        Patient p = new Patient("John Wick", "jacksonheights@pm.me", "1-800-267-2001");

        List<CareProvider> cps = new ArrayList<>();
        String[] names = {"Spider-man", "Superman", "Sassy-man"};
        String[] emails = {"55@gmail.com", "66@yahoo.ca", "77@aol.com"};
        String[] phones = {"292-584-3443", "333-654-5439", "194-430-4856"};
        for (int i = 0; i < names.length; i++){
            cps.add(new CareProvider(names[i], emails[i], phones[i]));
        }
        p.setCareProviders(cps);

        Problem p1 = new Problem("title", "descr", p.getId());
        Problem p2 = new Problem("title2", "descr2", p.getId());
        p.setProblems(Arrays.asList(p1, p2));

        List<Record> records = new ArrayList<>();
        List<Comment> comments = new ArrayList<>();
        List<BodyPhoto> bps = new ArrayList<>();
        for (Problem problem : p.getProblems()) {

            Record r1 = new Record(problem.getId(), "", "", null, null, null);
            Record r2 = new Record(problem.getId(), "", "", null, null, null);
            records.add(r1);
            records.add(r2);
            problem.getRecords().add(r1);
            problem.getRecords().add(r2);

            Comment c1 = new Comment(problem.getId(), "", "", "");
            Comment c2 = new Comment(problem.getId(), "", "", "");
            comments.add(c1);
            comments.add(c2);
            problem.addComment(c1);
            problem.addComment(c2);

            // Body Photo test omitted because not testable.
            // Runtime except as follows:
            //      Method decode in android.util.Base64 not mocked.
            //      See http://g.co/androidstudio/not-mocked for details.
            /*
            BodyPhoto bp1 = new BodyPhoto(problem.getId(), "");
            BodyPhoto bp2 = new BodyPhoto(problem.getId(), "");
            bps.add(bp1);
            bps.add(bp2);
            problem.addBodyPhoto(bp1);
            problem.addBodyPhoto(bp2);
            */

        }

        // Body Photo test omitted because not testable.
        // See above.
        /*
        List<RecordPhoto> rps = new ArrayList<>();
        for (Record record: records) {
            RecordPhoto rp1 = new RecordPhoto(record.getId(), "", 1, 1);
            RecordPhoto rp2 = new RecordPhoto(record.getId(), "", 12, 12);
            rps.add(rp1);
            rps.add(rp2);
            record.addPhoto(rp1);
            record.addPhoto(rp2);
        }
        */

        return p;

    }

}
