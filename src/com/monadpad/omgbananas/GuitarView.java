package com.monadpad.omgbananas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * User: m
 * Date: 11/15/13
 * Time: 11:01 PM
 */
public class GuitarView extends View {

    private Paint paint;
    private Paint paintOff;

    private int width = -1;
    private int height = -1;

    private int boxWidth;
    private int boxHeight;

    private Jam mJam;
    private Channel mChannel;

    private Paint topPanelPaint;
    private Paint paintText;

    private int firstRowButton = 0;

    private Paint paintBeat;

    private int touchingString = -1;
    private int touchingFret = -1;

    private Note restNote;

    private int lastFret = -1;

    private int key;
    private int[] scale;

    private int frets = 0;

    private int[] fretMapping;
    private int[] noteMapping;

    private String[] keyCaptions = {"C", "C#", "D", "Eb", "E", "F", "F#", "G", "G#", "A", "Bb", "B"};

    private boolean useScale = true;



    private int draw_lastDrawnX = 0;
    private Note draw_note;
    private float draw_x;
    private Bitmap draw_noteImage;
    private int draw_boxwidth;

    private Bitmap images[][];

    private Paint paintCurrentBeat;


    private int lowNote;

    private boolean modified = false;

    public GuitarView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        paint.setARGB(255, 255, 255, 255);
        paint.setShadowLayer(10, 0, 0, 0xFFFFFFFF);

        paintText = new Paint();
        paintText.setARGB(255, 255, 255, 255);
        paintText.setTextSize(24);

        paintBeat =  new Paint();
        paintBeat.setARGB(255, 255, 0, 0);

        paintOff = new Paint();
        paintOff.setARGB(255, 128, 128, 128);
        //paintOff.setShadowLayer(10, 0, 0, 0xFFFFFFFF);
        paintOff.setStyle(Paint.Style.STROKE);
        paintOff.setTextSize(paintText.getTextSize());

        topPanelPaint = new Paint();
        topPanelPaint.setARGB(255, 192, 192, 255);

        setBackgroundColor(Color.BLACK);

        restNote = new Note();
        restNote.setRest(true);


        images = new Bitmap[8][2];
        images[0][0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.w_note_half);
        images[0][1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.w_note_rest_half);
        images[1][0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.w_note_dotted_quarter);
        images[1][1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.w_note_rest_dotted_quarter);
        images[2][0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.w_note_quarter);
        images[2][1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.w_note_rest_quarter);
        images[3][0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.w_note_dotted_eighth);
        images[3][1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.w_note_rest_dotted_eighth);
        images[4][0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.w_note_eighth);
        images[4][1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.w_note_rest_eighth);
        images[5][0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.w_note_dotted_sixteenth);
        images[5][1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.w_note_rest_dotted_sixteenth);
        images[6][0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.w_note_sixteenth);
        images[6][1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.w_note_rest_sixteenth);
        images[7][0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.w_note_thirtysecond);
        images[7][1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.w_note_rest_thirtysecond);

        paintCurrentBeat = new Paint();
        paintCurrentBeat.setARGB(128, 255, 0, 0);
        paintCurrentBeat.setShadowLayer(4, 0, 0, 0xFFFFFFFF);
        paintCurrentBeat.setStyle(Paint.Style.FILL_AND_STROKE);

    }

    public void onDraw(Canvas canvas) {

        //if (height != getHeight()) {
            width = getWidth();
            height = getHeight();
            boxHeight = height / frets;
        //}

        int playingNote = mChannel.getPlayingNoteNumber();
        int noteNumber;

        for (int fret = 1; fret <= frets; fret++) {
            canvas.drawLine(0, height - fret * boxHeight, width, height - fret * boxHeight, paint);
            noteNumber = fretMapping[fret - 1];
            canvas.drawText(keyCaptions[noteNumber % 12], 0, height - (fret - 1) * boxHeight, paint);

            if (noteNumber == playingNote) {
                canvas.drawRect(0, height - fret * boxHeight,
                        width, height - (fret - 1) * boxHeight,
                        topPanelPaint);

            }
        }


        if (touchingFret > -1 ) {
            canvas.drawRect(0, height - (touchingFret + 1) * boxHeight,
                    width, height - touchingFret  * boxHeight,
                    topPanelPaint);

        }

        drawNotes(canvas, mChannel.getNotes());
    }



    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN ||
                action == MotionEvent.ACTION_MOVE) {

            if (!modified) {
                modified = true;

                mChannel.clearNotes();
            }

            if (!mChannel.enabled)
                mChannel.enable();

            touchingString = (int)Math.floor(event.getX() / boxWidth);
            touchingFret = (int)Math.floor(event.getY() / boxHeight);
            touchingFret = Math.max(0, Math.min(fretMapping.length - 1, touchingFret));

            touchingFret = fretMapping.length - touchingFret - 1;

            if (touchingFret != lastFret) {
                lastFret = touchingFret;
                Note note = new Note();
                note.setNote(fretMapping[touchingFret]);
                note.setInstrumentNote(fretMapping[touchingFret] - lowNote);
                mChannel.playNote(note);
            }


        }
        if (action == MotionEvent.ACTION_UP) {
            mChannel.playNote(restNote);
            touchingString = -1;
            touchingFret = -1;
            lastFret = -1;
        }

        invalidate();
        return true;
    }

    public void setJam(Jam jam, Channel channel) {
        mJam = jam;
        mChannel = channel;
        setScaleInfo();

        mJam.addInvalidateOnBeatListener(this);

    }

    public void setScaleInfo() {

        key = mJam.getKey();
        scale = mJam.getScale();
        frets = 0;

        useScale = mChannel.isAScale();

        if (!useScale) {
            key = 0;
        }
        lowNote = mChannel.getLowNote();
        int highNote = mChannel.getHighNote();
        int[] allFrets = new int[highNote - lowNote + 1];
        noteMapping = new int[highNote - lowNote + 1];

        int s;
        boolean isInScale;
        for (int i = lowNote; i <= highNote; i++) {
            isInScale = false;

            for (s = 0; s < scale.length; s++) {
                if (!useScale || scale[s] == ((i - key) % 12)) {
                    isInScale = true;
                    break;
                }

            }

            if (isInScale) {
                noteMapping[i - lowNote] = frets;
                allFrets[frets++] = i;
            }

        }

        fretMapping = new int[frets];
        for (int i = 0; i < frets; i++) {
            fretMapping[i] = allFrets[i];
        }


    }

    public void drawNotes(Canvas canvas, ArrayList<Note> list) {

        float middle = getHeight() / 2.0f;
        float draw_y = 30.0f;
        draw_boxwidth = images[0][0].getWidth();
        draw_lastDrawnX = draw_boxwidth / 2;

        for (int j = 0; j < list.size(); j++) {

            draw_note = list.get(j);

/*            if (draw_note.getBeatPosition() % 4 == 0)
                canvas.drawLine(draw_lastDrawnX, draw_y,
                        draw_lastDrawnX, draw_y + boxHeight, paint);
*/
            draw_x = (float)(draw_lastDrawnX);
            draw_lastDrawnX += draw_boxwidth;

            draw_noteImage = null;
            if (draw_note.getBeats() == 2.0d) {
                draw_noteImage = images[0][draw_note.isRest() ? 1 : 0];
            }
            if (draw_note.getBeats() == 1.5d) {
                draw_noteImage = images[1][draw_note.isRest() ? 1 : 0];
            }
            if (draw_note.getBeats() == 1.0d) {
                draw_noteImage = images[2][draw_note.isRest() ? 1 : 0];
            }
            if (draw_note.getBeats() == 0.75d) {
                draw_noteImage = images[3][draw_note.isRest() ? 1 : 0];
            }
            if (draw_note.getBeats() == 0.5d) {
                draw_noteImage = images[4][draw_note.isRest() ? 1 : 0];
            }
            if (draw_note.getBeats() == 0.375d) {
                draw_noteImage = images[5][draw_note.isRest() ? 1 : 0];
            }
            if (draw_note.getBeats() == 0.25d) {
                draw_noteImage = images[6][draw_note.isRest() ? 1 : 0];
            }
            if (draw_note.getBeats() == 0.125d) {
                draw_noteImage = images[7][draw_note.isRest() ? 1 : 0];
            }

            draw_y = getHeight() / 2;
            if (!draw_note.isRest()) {
                draw_y = (frets - 1 - noteMapping[draw_note.getInstrumentNote()]) * boxHeight;
            }

            if (draw_note.isPlaying()) {
                canvas.drawRect(draw_x, draw_y,
                        draw_lastDrawnX,
                        draw_y + boxHeight,
                        paintCurrentBeat);
            }

            if (draw_noteImage != null) {
                canvas.drawBitmap(draw_noteImage, draw_x , draw_y, null);
            }
            else {
                canvas.drawText(Double.toString(draw_note.getBeats()),
                        draw_x, draw_y + 50,
                        paint);
            }

        }

    }

}
