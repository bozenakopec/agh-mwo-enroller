
package com.company.enroller.controllers;

import com.company.enroller.model.Participant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.company.enroller.model.Meeting;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;

import java.util.Collection;


@RestController
@RequestMapping("/meeting")
public class MeetingRestController {

    @Autowired
    MeetingService meetingService;

    @Autowired
    ParticipantService participantService;


    //  pobieranie listy wszystkich spotkań
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetings() {
        Collection<Meeting> meetings = meetingService.getAll();
        return new ResponseEntity<>(meetings, HttpStatus.OK);
    }

    // pobieranie pojedynczego spotkania

    @RequestMapping(value = "/id={id}", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetingById(@PathVariable("id") long id) {
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return new ResponseEntity<String>("Meeting" + meetingService.findById(id) + " does not exist", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(meeting, HttpStatus.OK);
    }


    // dodawanie spotkań
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> registerNewMeeting(@RequestBody Meeting meeting) {
        Meeting createNewMeeting = meetingService.findById(meeting.getId());
        if (createNewMeeting != null) {
            return new ResponseEntity<String>("Meeting" + meetingService.findById(meeting.getId()) + "has been already created ", HttpStatus.CONFLICT);
        }
        meetingService.add(meeting);
        return new ResponseEntity<>(createNewMeeting, HttpStatus.OK);
    }


    // dodawanie uczestnika do spotkania
    @RequestMapping(value = "/{id}/addparticipants", method = RequestMethod.PUT)
    public ResponseEntity<?> addParticipanttoMeeting(@PathVariable("id") long id, @RequestBody Participant participant) {
        Meeting meeting = meetingService.findById(id);
        Participant thisParticipant = participantService.findByLogin(participant.getLogin());
        if (meeting == null || thisParticipant == null) {
            return new ResponseEntity<String>("Sorry, meeting " + meetingService.findById(id) + "or participant " + participantService.findByLogin(participant.getLogin()) + " does not exist", HttpStatus.NOT_FOUND);
        }
        meeting.addParticipant(thisParticipant);
        meetingService.update(meeting);

        return new ResponseEntity<>(meeting, HttpStatus.OK);
    }

    // pobieranie uczestników ze spotkania
    @RequestMapping(value = "/{id}/participants", method = RequestMethod.GET)
    public ResponseEntity<?> getParticipantsFromMeeting(@PathVariable("id") long id) {
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return new ResponseEntity<>("Sorry, ID" + meetingService.findById(id) + "is not correct. Type the right one, please", HttpStatus.CONFLICT);
        }
        return new ResponseEntity<Collection<Participant>>(meeting.getParticipants(), HttpStatus.OK);
    }



    // usuwanie uczestnika ze spotkania
    @RequestMapping(value = "/{id}/deleteparticipants", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteParticipantFromMeeting(@PathVariable("id") long id, @RequestBody Participant participant) {
        Meeting meeting = meetingService.findById(id);
        Participant thisParticipant = participantService.findByLogin(participant.getLogin());
        if (meeting == null || thisParticipant == null) {
            return new ResponseEntity<String>("Sorry, but meeting " + meetingService.findById(id) + "or participant " + participantService.findByLogin(participant.getLogin()) + "cannot be removed", HttpStatus.NOT_FOUND);
        }
        meeting.removeParticipant(thisParticipant);
        meetingService.update(meeting);
        return new ResponseEntity<>(meeting, HttpStatus.OK);
    }

    // usuwanie spotkań
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteMeeting(@PathVariable("id") long id) {
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return new ResponseEntity<String>("This meeting does not exist", HttpStatus.NOT_FOUND);
        }
        meetingService.delete(meeting);
        return new ResponseEntity<>("The meeting" + meetingService.findById(id) + "has been deleted", HttpStatus.OK);
    }

}
