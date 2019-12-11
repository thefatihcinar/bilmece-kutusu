package com.example.user.bilmece;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class Game extends AppCompatActivity {

    ArrayList<Bilmece> bilmeceler;
    // all of the Bilmeceler - all of them

    Button[] buttons;
    // array of the 4 answer buttons

    TextView textViewSoru;
    // the text view for BILMECE

    int indexOfTheRightAnswer;
    // This is the index of the buttons that the right answer is at

    int numberOfBilmece;
    // How many bilmece(riddles) are there in the array

    int whichQuestionProgramIsAt;
    // this is the index of the question that the app is currently at

    ArrayList<Integer> virginQuestions;
    // This is the array of indexes of the bilmeceler that have not been asked to the user


    int Score;
    // score of the user
    // one right question 10 points

    int Level;
    // every 30 points is One level

    ArrayList<Integer> Colors;
    // Colors array list for chosing a random color for LEVEL Background
    // in level-up case

    TextView textViewLevel;
    // LEVEL text view

    TextView textViewScore;
    // SCORE text view

    MediaPlayer mediaPlayer;
    // sound operations -> Level Up, Right Answer, Wrong Answer

    MediaPlayer mediaPlayerBackgroundMusic;
    // background music

    boolean Clickable;
    // if the user has answered a question
    // but are currently waiting for a response
    // LOCK the options


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent incomingIntent = getIntent();

        int whereMusicLeft = incomingIntent.getIntExtra("music_information",0);
        // learn where the music left and start


        mediaPlayerBackgroundMusic = MediaPlayer.create(this, R.raw.background_music);
        mediaPlayerBackgroundMusic.seekTo(whereMusicLeft); // go there
        mediaPlayerBackgroundMusic.start();

        // UPDATE the status bar color with blue
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.statusBarBlue));
        }

        textViewLevel = (TextView) findViewById(R.id.textViewLevel);
        textViewScore = (TextView) findViewById(R.id.textViewScore);

        bilmeceler = new ArrayList<Bilmece>();
        // create a new arraylist for BİLMECELER

        CreateAndConstructBilmeceler();
        // WE HAVE TO CREATE AND CONSTRUCT ALL THE BILMECELER arraylist
        // this function will do this

        numberOfBilmece = bilmeceler.size();
        // learn the number of bilmeceler in advance

        /* THIS IS SUPER IMPORANT */

        // Initilize and Create Array of Virgin Questions

        Log.i("Number of bilmece: ", String.valueOf(bilmeceler.size()));

        virginQuestions = new ArrayList<Integer>();
         /*
            Why do we need array of integers called Virgin Questions?
            To randomize the quesions, and while doing this
            We can't create a new arraylist of bilmeceler for the sake of memory
            So we use indexes to get random questions
            and we remove one index once it is asked to the user
         */

        for(int m = 0; m < numberOfBilmece; m++){
            virginQuestions.add(Integer.valueOf(m));
        }
        // At start, all the questions are not ASKED
        // All of them are virgin questions

        Clickable = true;
        // at first the user can click to the buttons
        // THE LOCK MECHANISM works once the user choses one of the options


        /*
            Initializing and Consturcting the Button Array
        */

        buttons = new Button[4];

        buttons[0] = (Button) findViewById(R.id.buttonYanit0);
        buttons[1] = (Button) findViewById(R.id.buttonYanit1);
        buttons[2] = (Button) findViewById(R.id.buttonYanit2);
        buttons[3] = (Button) findViewById(R.id.buttonYanit3);

        /*
            Why do we need button array?
            To change colors, all the function must know their ids and references to them
         */


        textViewSoru = (TextView) findViewById(R.id.textViewSoru);

        Score = 0;
        // score of the user
        // one right question 10 points

        Level = 0;
        // every 30 points is One level

        InitializeColors();
        // create the colors array
        // to assign a color to the new level each time
        // go to the array


        // initially programs DOES NOT start with the first question

        // get the first question RANDOMLY
        int new_question = NewQuestion();

        UpdateTheScreen(new_question);
        // Fetch/Update the screen based on this question
        // Buttons, The question

        UpdateLevel();
        // UPDATE LEVEL TABLE on the screen
        UpdateScore();
        // UPDATE SCORE TABLE on the screen


    }

    public void InitializeColors(){
        /*
            This function initialises and creates THE COLORS ARRAY
            We will use it, to determine the color of the new level RANDOMLŞ
         */

        Colors = new ArrayList<Integer>();

        Colors.add(Color.parseColor("#8a2be2"));
        Colors.add(Color.parseColor("#ff4040"));
        Colors.add(Color.parseColor("#794044"));
        Colors.add(Color.parseColor("#ff1493"));
        Colors.add(Color.parseColor("#000080"));
        Colors.add(Color.parseColor("#00ced1"));
        Colors.add(Color.parseColor("#008000"));
        Colors.add(Color.parseColor("#0000ff"));
        Colors.add(Color.parseColor("#ffd700"));
        Colors.add(Color.parseColor("#420420"));
        Colors.add(Color.parseColor("#008080"));
        Colors.add(Color.parseColor("#00ffff"));


    }

    public void UpdateScore(){
        /*
            This function updates THE SCORE TABLE on the screen
         */

        textViewScore.setText("Skor : " + String.valueOf(Score));
    }


    public boolean UpdateLevel(){
        /*
            This method updates THE LEVEL TABLE on the screen
            And checks if its a level-up or not
            If it's level-up, it makes necessary adjustments
         */

        textViewLevel.setText("Level:  " + String.valueOf(Level));

        int levelBefore = Level;
        // Learn the level before the level is NOT UPDATED

        Level = Score / 30;
        // UPDATE THE LEVEL, score has already been updated
        // this is the level now

        if(Level > levelBefore){
            // this means new level

            mediaPlayerBackgroundMusic.pause();

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    textViewLevel.setText("Level:  " + String.valueOf(Level));

                    mediaPlayer = MediaPlayer.create(Game.this, R.raw.level_up);
                    // make the level-up sound effect
                    mediaPlayer.start();

                    Random rand = new Random();

                    int randomColor = rand.nextInt(Colors.size());
                    // RANDOMLY assing a new color to the LEVEL BOARD

                    textViewLevel.setBackgroundColor(Colors.get(randomColor));



                }
            }, 2000);

            return true; // if it's a level-up, tell this to the calling function


        }

        return false;
    }

    public void UpdateTheAnswerButtons(int whichQuestion){
        /*
            This method will update the answer buttons
            required input: whichQuestion -> In Which Question Are We?
            RANDOMIZATION OF BUTTONS ALGORITHM
         */

        // Learn the answer of the question

        String theAnswer = bilmeceler.get(whichQuestion).getYanit();

        // Decide which button you are going to assign the right answer

        Random rand = new Random();
        // Generate random integers in range 0 to 4
        int rightAnswer = rand.nextInt(4);
        // right answer is at this index

        buttons[rightAnswer].setText(theAnswer);

        // Remember the right answer

        indexOfTheRightAnswer = rightAnswer;

        // Then ASSIGN RANDOM ANSWERS to the other buttons

        int numberOfButtons = 4;

        String randomAnswer;
        // string for random answers

        for(int i = 0; i < numberOfButtons; i++ ){
            if(i == rightAnswer){
                buttons[i].setBackgroundColor(getResources().getColor(R.color.newExantricBlue));
                continue;
            }
            else{
                // Get a random index
                // and then get the answer at that index
                int randomIndex = rand.nextInt(numberOfBilmece);
                while (randomIndex == whichQuestion){randomIndex = rand.nextInt(numberOfBilmece);}
                // if it overlaps with the current question, get new random index

                // get the answer at this random index
                randomAnswer = bilmeceler.get(randomIndex).getYanit();
                buttons[i].setText(randomAnswer);
                buttons[i].setBackgroundColor(getResources().getColor(R.color.newExantricBlue));
            }

        }

    }

    public void UpdateTheScreen(int whichQuestion){
        /*
            This method will update the screen
            Fetches the question to the screen
            And updates the buttons
            Requires: In which question we are at?
         */
        // go get the question

        String theQuestion = bilmeceler.get(whichQuestion).getSoru();
        textViewSoru.setText(theQuestion);

        // Update the question
        // and then update the buttons

        UpdateTheAnswerButtons(whichQuestion);

    }

    public void Control(View viewOnTheScreen){
        /*
            This method controls the options i.e. Buttons
            and will determine whether the user has answered correctly
            or wrong   [RESPONSE]
            And then updates the screen with the next Question
         */



        if(!Clickable) return;
        /*
            This means,
            if the buttons are LOCKED,
            i.e. If the user has answered the question and waits for the RESPONSE
            LOCK all the buttons.
            Otherwise, it causes bugs!
         */

        // Cast the incoming view to button to get its tag
        final Button touchButton = (Button) viewOnTheScreen;
        Clickable = false; // THE USER HAS TOUCHED! LOCK!

        // and then learn its tag, i.e. which button is clicked

        final int givenAnswer = Integer.parseInt(touchButton.getTag().toString());

        mediaPlayer = MediaPlayer.create(Game.this,R.raw.touch_sound);
        // make the touch sound
        mediaPlayer.start();

        mediaPlayerBackgroundMusic.pause();
        // when clicked, pause the background sound,
        // because the user will wait for response

        touchButton.setBackgroundColor(getResources().getColor(R.color.waitingYellow));
        // MAKE THE TOUCHED BUTTON YELLOW
        // the user starts waiting

        new Handler().postDelayed(new Runnable() {
            public void run() {
                // THE USER WAITS FOR 3 SECONDS

                // After 3 seconds, Control Starts
                if (givenAnswer == indexOfTheRightAnswer){
                    // IF THE USER IS RIGHT

                    touchButton.setBackgroundColor(getResources().getColor(R.color.rightAnswerGreen));
                    // MAKE THE TOUCHED BUTTON GREEN


                    mediaPlayer = MediaPlayer.create(Game.this, R.raw.right_answer);
                    mediaPlayer.start();
                    // make a victory sound

                    Score += 10;  // 10 scores
                    final boolean isItLevelUp = UpdateLevel();
                    // and UPDATE_LEVEL TABLE, and check whether is a level-up or not
                    UpdateScore(); // UPDATE SCORE TABLE


                    new Handler().postDelayed(new Runnable() {
                        public void run() {

                            // LEARN THE NEW QUESTION
                            int new_question = NewQuestion();

                            UpdateTheScreen(new_question);
                            // UPDATE THE SCREEN BASED ON THE NEW QUESTION

                            // AFTER UPDATE, OR YOU CAN DO THIS BEFORE
                            // CHECK IF IT'S A LEVEL-UP
                            if (isItLevelUp){
                                new Handler().postDelayed(new Runnable() {
                                    public void run() {
                                        // After 7 seconds
                                        // in this 7 seconds, the user will experience the level-up
                                        // with sound and graphics
                                        Clickable = true;  // UNLOCK THE BUTTONS AFTER THIS
                                        mediaPlayerBackgroundMusic.start(); // WE'RE IN THE NEW QUESTION
                                        // so start the background music again
                                    }
                                }, 6000);
                                return;
                            }
                            else
                                // ELSE IF IT IS NOT A LEVEL-UP
                                mediaPlayerBackgroundMusic.start();
                                // just start the background music again

                                mediaPlayer.release(); // release the sound

                                Clickable = true; // UNLOCK THE BUTTONS
                            return;
                        }
                    }, 2700);



                }
                else{
                    // IF THE USER IS WRONG!

                    touchButton.setBackgroundColor(getResources().getColor(R.color.wrongAnswerRed));
                    // MAKE THE TOUCHED BUTTON RED, BECAUSE IT'S WRONG
                    buttons[indexOfTheRightAnswer].setBackgroundColor(getResources().getColor(R.color.rightAnswerGreen));
                    // AND REVEL THE RIGHT ANSWER TO THE USER
                    // BY MAKING THE BUTTON GREEN


                    mediaPlayer = MediaPlayer.create(Game.this,R.raw.wrong_answer);
                    mediaPlayer.start();
                    //  make FAILURE SOUND

                    // AFTER 3.2 SECONDS, NEW QUESTION OPERATIONS
                    new Handler().postDelayed(new Runnable() {
                        public void run() {

                            int new_question = NewQuestion();

                            UpdateTheScreen(new_question);

                            mediaPlayerBackgroundMusic.start();
                            Clickable = true; // UNLOCK THE BUTTONS

                            mediaPlayer.release(); // release the sound
                            return;
                        }
                    }, 3200);


                }
            }
        }, 3000);


    }



    public int NewQuestion(){
        /*
            This question RANDOMLY choses new bilmece
            But it is smart enough not to chose the one that is chosen before
            it updates "whichQuestion" class variable
         */

        if(virginQuestions.size() < 5){
            /*
                This means that there is very few question left in the virgin questions arraylist
                Almost all the questions have been asked to the user!
                So, you can now ask the same question again !!

             */

            // Fetch all the questions again to the algorithm, TO ASK ONE MORE TIME
            for(int X = 0; X < numberOfBilmece; X++){
                virginQuestions.add(Integer.valueOf(X));
            }
        }

        Random rand = new Random();

        int randomlyChosenIndex = rand.nextInt(virginQuestions.size());

        // Get the index for the bilmece at the randomly chosen index

        int randomIndexForBilmece = virginQuestions.get(randomlyChosenIndex);
        // this is the index for bilmece that has been asked before

        whichQuestionProgramIsAt = randomIndexForBilmece;
        // program will continue with this question

        // and delete this index from the virgin questions arraylist
        // becase we will ask this question right now
        // and in order not to ask again
        virginQuestions.remove(randomlyChosenIndex);


        return randomIndexForBilmece;
        // maybe we can use this


    }

    public void Finish(){

        finish(); // call onDestroy()

        // GO TO THE RESULT ACTIVITY
        Intent intent = new Intent(Game.this, Result.class);

        // tell the score to the new activity
        intent.putExtra("skor",Score);

        startActivity(intent);

    }


    public void FinishTheGame(View view){
        /*
            If finish button is pressed,
            this method will invoke
            and finishes the game
         */
        Finish();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayerBackgroundMusic.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayerBackgroundMusic.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mediaPlayerBackgroundMusic.stop();
        // stop the background music
        mediaPlayerBackgroundMusic.release();
        // release the backgroud music

        if(mediaPlayer != null) mediaPlayer.release();
        // if the media player is not null release it
        /*
            In which case, media player might be null?
            If the user has never answered a question and wants to finish the game
            in that case mediaplayer would be null
            And this code protects the app from this bug
         */

    }

    public void CreateAndConstructBilmeceler(){

         /*
            This method will conscruct the Bilmece ArrayList from Strach
         */

        Bilmece iterator; // reference to a new bilmece object

        iterator = new Bilmece();
        iterator.setSoru("Bir küçük fıçıcık, içi dolu turşucuk.");
        iterator.setYanit("Limon");
        bilmeceler.add(iterator);


        iterator = new Bilmece();
        iterator.setSoru("Gökte gördüm köprü, rengi yedi türlü.");
        iterator.setYanit("Gökkuşağı");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Bir sapı var, yüz topu var.");
        iterator.setYanit("Üzüm");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Bilgi verir herkese, En güzel dosttur bize.");
        iterator.setYanit("Kitap");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("İki yuvarlak biri büyük biri küçük onlar olmasa bize dünya kapkara.");
        iterator.setYanit("Göz ve Göz bebeği");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Yer altında sakallı kök");
        iterator.setYanit("Pırasa");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Kuyruğu var, at değil. Kanadı var, kuş değil.");
        iterator.setYanit("Balık");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Dışı var, içi yok; Tekme yer, suçu yok.");
        iterator.setYanit("Top");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Akşam baktım çok idi, Sabah baktım yok idi.");
        iterator.setYanit("Yıldız");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Çarşıdan aldım bir tane, eve geldim bin tane.");
        iterator.setYanit("Nar");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Benim bir hayvanım var kuyruğundan uzun burnu var.");
        iterator.setYanit("Fil");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Gökte durur paslanmaz, suya düşer ıslanmaz.");
        iterator.setYanit("Güneş");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Ne kanı var ne canı, beş tanedir parmağı.");
        iterator.setYanit("Eldiven");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Dereler tepeler, şık şık küpeler.");
        iterator.setYanit("Kiraz");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Çocukların yuvası, bilgi doludur orası.");
        iterator.setYanit("Okul");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Kümeslerin efesi, her sabah çınlar sesi, uyandırır herkesi.");
        iterator.setYanit("Horoz");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Dal üstünde al yanak. İnanmazsan ye de bak.");
        iterator.setYanit("Elma");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Sarı mendil mavi denize düşerse ne olur?");
        iterator.setYanit("Islanır");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Daldan dala kırmızı pala.");
        iterator.setYanit("Sincap");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Ay varken uçar, gün varken kaçar.");
        iterator.setYanit("Yarasa");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Yeraltında yağlı kayış.");
        iterator.setYanit("Yılan");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Kanadı var, kuş değil. Boynuzu var, koç değil.");
        iterator.setYanit("Kelebek");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Çiçek onun, dal onun yediğimiz bal onun iğnesi var batırır kanadı var götürür.");
        iterator.setYanit("Arı");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Gelişi aslan gibi, duruşu kaplan gibi, yayılır hasır gibi, sürünür esir gibi.");
        iterator.setYanit("Kedi");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Karşıdan baktım hiç yok yanına vardım pek çok.");
        iterator.setYanit("Karınca");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Karşıdan gördüm bir taş yanına vardım dört ayak bir baş.");
        iterator.setYanit("Kaplumbağa");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Yer altında yuvası var, fırça gibi dikeni var.");
        iterator.setYanit("Kirpi");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Alçacık dallı yemesi ballı.");
        iterator.setYanit("Çilek");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Evi sırtında, ayağı karnında. İzi yıldız, gözleri boynuz.");
        iterator.setYanit("Salyangoz");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Hangi kalemle yazı yazılmaz?");
        iterator.setYanit("Kontrol Kalemiyle");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("İnim inim inler, cümle alem dinler.");
        iterator.setYanit("Davul");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Dalda durur, elde durmaz.");
        iterator.setYanit("Kuş");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Kat kat çiçek, yemeği yenecek.");
        iterator.setYanit("Karnabahar");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Bir kızım var, kat kat çeyizi var.");
        iterator.setYanit("Lahana");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Kat kat katmer değil, yenir ama meyve değil.");
        iterator.setYanit("Soğan");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Alçacık boyu var, mor kadifeden donu var.");
        iterator.setYanit("Patlıcan");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Arabadan atladı, pantolonu patladı.");
        iterator.setYanit("Karpuz");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Hanım içerde, saçı dışarıda.");
        iterator.setYanit("Mısır");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Daldan dala atlarım, kuyruğumdan sarkarım.");
        iterator.setYanit("Maymun");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Dağdan gelir arık arık, ayağın da demir çarık.");
        iterator.setYanit("At");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("O odanın içinde, oda onun içinde.");
        iterator.setYanit("Ayna");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Deniz üstünde, yufka açar.");
        iterator.setYanit("Dalga");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("O benim babam ama ben onun kızı değilim ben onun neyiyim?");
        iterator.setYanit("Oğlu");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Suyu tuzlu içilmez vapursuz hiç geçilmez rüzgarlar çok eserse dalgaları eksilmez.");
        iterator.setYanit("Deniz");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Zenginin elinde fakirin dilinde.");
        iterator.setYanit("Para");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Ben durursam hayat durur.");
        iterator.setYanit("Kalp");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Sabahları açılır geceleri kapanır.");
        iterator.setYanit("Perde");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Mini mini kuşlar her yeri taşlar.");
        iterator.setYanit("Dolu");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Adı olan ama soyadı olmayan şehir hangisidir?");
        iterator.setYanit("Adıyaman");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Bize ait olduğu halde başkalarının kullandığı şey nedir?");
        iterator.setYanit("Adımız");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Kırılınca mutlu olduğumuz şey nedir?");
        iterator.setYanit("Rekor");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Bilmece bildirmece buz üstünde kaydırmaca.");
        iterator.setYanit("Paten");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Sesi var canı yok, konuşuyor ağzı yok.");
        iterator.setYanit("Radyo");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Eve bitişik odada yemek pişer orada.");
        iterator.setYanit("Mutfak");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Var ama göremeyiz, olmazsa bilemeyiz.");
        iterator.setYanit("Akıl");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Yaz yaz bitmez.");
        iterator.setYanit("Yazı");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Duruşu ömür, gözleri kömür, soğuk dondurur sıcak öldürür.");
        iterator.setYanit("Kardan adam");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Yemek için alınır ama yenmez bilin bakalım bu nedir?");
        iterator.setYanit("Tabak");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Çıtır çıtır yenir bunun adı nedir?");
        iterator.setYanit("Çekirdek");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Zor kazanılır ama kolay kaybedilir bilin bakalım bu nedir?");
        iterator.setYanit("Arkadaş");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("İki cam bir araya gelirse ne olur?");
        iterator.setYanit("Gözlük");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Sopalarla kurarsın akşam içinde yatarsın.");
        iterator.setYanit("Çadır");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Elde yapılır, kulağa asılır.");
        iterator.setYanit("Küpe");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Kızlar tuvaletine erkek girerse ne olur?");
        iterator.setYanit("ayıp olur");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Evimizi sıcak tutar dumanınını dışarı atar.");
        iterator.setYanit("Soba");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("En çok acı çeken dağ hangisidir?");
        iterator.setYanit("Ağrı Dağı");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Soyulur ama yenmez.");
        iterator.setYanit("Banka");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Altında dört teker üstünde yük çeker.");
        iterator.setYanit("Araba");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Bilmece bildirmece tırnak üstünde kaydırmaca.");
        iterator.setYanit("Oje");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Üstünde yatılır yatak değil, üstünde oturulur sandalye değil.");
        iterator.setYanit("Kanepe");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Serin beni gece gündüz, basın bana gece gündüz.");
        iterator.setYanit("Halı");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Benim iki pencerem var, etrafı etten duvar, her gün erkenden açarım, gece olunca kaparım.");
        iterator.setYanit("Göz");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Sıra sıra odalar birbirini kovalar.");
        iterator.setYanit("Tren");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Dokunmadan ne tutulur?");
        iterator.setYanit("Oruç");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Bir kapaklı, çok yapraklı, içindeki bilgiler alfabetik sıralı.");
        iterator.setYanit("Sözlük");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Kanadı var kuş değil yolları yokuş değil.");
        iterator.setYanit("Uçak");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Bir hizmetçim var, otuz iki kişinin temizliğine bakar.");
        iterator.setYanit("Diş fırçası");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Şekere benzer tadı yok gökte uçar kanadı yok.");
        iterator.setYanit("Kar");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Yer altında turuncu kazık bunu bilinmeyene yazık.");
        iterator.setYanit("Havuç");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Dalgalanır göklerde, sevgisi yüreklerde.");
        iterator.setYanit("Bayrak");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Ele akar, hoş kokar.");
        iterator.setYanit("Kolonya");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Kimseye görünmeden sınıftan çıkabilen şey nedir?");
        iterator.setYanit("Gürültü");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Gökte açık pencere, kalaylı bir tencere.");
        iterator.setYanit("Ay");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Rengarenk açar mis gibi kokar.");
        iterator.setYanit("Çiçek");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Bir Japon ne zaman Merhaba der?");
        iterator.setYanit("Türkçeyi öğrendiği zaman");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Boş gider dolu gelir ağzıma bir hoş gelir.");
        iterator.setYanit("Kaşık");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Hangi adamın derisi yoktur?");
        iterator.setYanit("Kardan adamın");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("En tatlı ay hangi aydır?");
        iterator.setYanit("Balayı");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("8 ine,k 9 tavuk, 2 horoz, 5 keçi, 18 koyun ne yapar?");
        iterator.setYanit("gürültü yapar");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Sıcağa koyma kurur, suya koyma köpürür.");
        iterator.setYanit("Sabun");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Ben giderim o kalır.");
        iterator.setYanit("Ayak izi");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Yaprak ama yeşil değil.");
        iterator.setYanit("Defter yaprağı");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Küçücük kutu içi insan dolu.");
        iterator.setYanit("Televizyon");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Kapıyı açar, kapamadan kaçar.");
        iterator.setYanit("Rüzgar");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Yemeğin başı hastanın aşı.");
        iterator.setYanit("Çorba");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Sivas da bir tane, Samsun da iki tane, Muğla da hiç yok.");
        iterator.setYanit("S");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Gül gibidir bize bakar hasta olunca çorba yapar.");
        iterator.setYanit("Anne");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Sabah çamur, akşam kömür.");
        iterator.setYanit("Kına");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Bize biraz ışık verir, sonra erir. Bilin bakalım bu nedir?");
        iterator.setYanit("Mum");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Ben giderim o gider, ben durum o  da durur.");
        iterator.setYanit("Gölge");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Açılırsa dünya olur, yakılırsa kül olur.");
        iterator.setYanit("Harita");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Ben giderim o gider, kolumda tak tak eder.");
        iterator.setYanit("Saat");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Ramazanda hangi et oruç bozmaz?");
        iterator.setYanit("Niyet");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("İnmesi güzel ama çıkması baya bir üzer.");
        iterator.setYanit("Merdiven");
        bilmeceler.add(iterator);



        /* PART 2 */


        iterator = new Bilmece();
        iterator.setSoru("Asla olmadım, daima olucağım, beni ne gören oldu ne de bulacak, yine de ben nefes alan ve yaşayan herkesin güvencesiyim. Peki ben neyim?");
        iterator.setYanit("Gelecek");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Yumurta dereden karşıya nasıl geçer?");
        iterator.setYanit("Tavuğun içinde");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Ortaya bir gümüş yüzük koydum, ay geldi alamadı, güneş geldi, aldı.");
        iterator.setYanit("Buz");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Ben varmadan o varır, her şeyden çok yol alır.");
        iterator.setYanit("Ses");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Sarı öküzün yattığı yerde ot bitmez.");
        iterator.setYanit("Ateş");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Söylemesi kolay bulması kolay olmayan şey .");
        iterator.setYanit("İş");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Ne kadar yağmur yağarsa yağsın daha çok ıslanmayan nedir?");
        iterator.setYanit("Su");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Ne kadar çoksa o kadar az görürsün.");
        iterator.setYanit("Karanlık");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Büyüğü yürümez, küçüğü büyümez.");
        iterator.setYanit("Taş");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Gözle görülmez elle tutulmaz ama gelip geçtiğini biliyoruz nedir bu?");
        iterator.setYanit("Zaman");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Yemez, içmez, susuz yerde durmaz.");
        iterator.setYanit("Köprü");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Köşede bulunur ama dünyayı dolaşır.");
        iterator.setYanit("Posta pulu");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Konuşursam o gider.");
        iterator.setYanit("Sessizlik");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Su gibi yumuşak, taş gibi sert.");
        iterator.setYanit("Toprak");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Yatağı var uyumaz ağzı var konuşmaz.");
        iterator.setYanit("Akarsu");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Mini mini minare içi sarı lale.");
        iterator.setYanit("Ampül");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Karınca kaderince yolda gider ince ince?");
        iterator.setYanit("Dikiş makinesi");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Dörtlü ayrılmaz, iki ikiyi takip eder, hepsi havaya muhtaç.");
        iterator.setYanit("Araba lastikleri");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Bir kuşum var düz uçar.");
        iterator.setYanit("Mermi");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Bir dalda iki leylek, biri iner biri kalkar.");
        iterator.setYanit("Terazi");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Herkesi doyurur, kendi doymaz.");
        iterator.setYanit("Fırın");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Her şeyi yer ama hiç doymaz.");
        iterator.setYanit("Ateş");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Başı var, kaşı yok, kuş türüdür tüyü çok.");
        iterator.setYanit("Kartal");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Tadım yoktur ama Dünya için önemliyim.");
        iterator.setYanit("Su");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Toprağa basmaz bacağı yok, halıya basar sahibi çok.");
        iterator.setYanit("Ev terliği");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Tek gözüm var dünyayı gezerim.");
        iterator.setYanit("Kamera");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Yol alır ama yolda da bırakır.");
        iterator.setYanit("Tekerlek");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Çok uzun her renk ne zaman bitecek bu renk?");
        iterator.setYanit("İplik");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Yukarı çıkar, aşağı inmez.");
        iterator.setYanit("Yaş");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Bir şey var her gün gelir.");
        iterator.setYanit("Uyku");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Tüyden bile hafif olan tutulması en zor şey nedir.");
        iterator.setYanit("Nefes");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Şehirden köye kadar giderim ama hiç hareket etmem.");
        iterator.setYanit("Yol");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Yukarıda küçük aşağıda büyük");
        iterator.setYanit("Uçak");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Yere düşüp kırılmayan, suya düşüp de ıslanmayan şey nedir?");
        iterator.setYanit("Işık");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Ağzı var yiyemez, saklar ama gizleyemez.");
        iterator.setYanit("Saklama kabı");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Yerin altında sarı benek.");
        iterator.setYanit("Altın");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Yer altında civcivli tavuk.");
        iterator.setYanit("Patates");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Varken yok olan, yokken var olan şey nedir?");
        iterator.setYanit("Korku");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Varma sakın yanına, on parmağın bal olur. Tutmak istersen yavaş tut, iki elin kan olur.");
        iterator.setYanit("Karadut");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Tüm insanların her zaman yaptığı şey nedir?");
        iterator.setYanit("Yaşlanmak");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Yeşil atlas, suda batmaz.");
        iterator.setYanit("Zeytinyağı");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Hangi dünyada yaşanmaz?");
        iterator.setYanit("Hayal dünyasında");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Yol biter o bitmez.");
        iterator.setYanit("Ufuk Çizgisi");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Dünyadaki herkes konuşunca beni kırıyor. Ben neyim?");
        iterator.setYanit("Sessizlik");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Koşuyor ama uçmuyor. Bu nedir?");
        iterator.setYanit("Deve kuşu");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Öne gitmiyor, arkaya da gitmiyor, ama bunu herkes kullanıyor?");
        iterator.setYanit("Asansör");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Ben giderim o gider ben kalırım o da kalır?");
        iterator.setYanit("Gölge");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Yüzlerce iğnesi var ama dikiş dikemez.");
        iterator.setYanit("Kirpi");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Örülmemiş duvarda doğmamış olan çocuğu oturur, ekilmemiş bostanı yolma diye bağırır.");
        iterator.setYanit("Yalan");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Yol üstünde kırmızı iplik.");
        iterator.setYanit("Solucan");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Yersen biterim, yemezsen yine biterim.");
        iterator.setYanit("Dondurma");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Mona Lisa tablosu nerede asılıdır?");
        iterator.setYanit("Duvarda");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Birisine gün içinde hep aynı soruyu sorarız fakat hep farklı cevap verir. Bu soru nedir?");
        iterator.setYanit("Saat kaç?");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Bir evim var, dolandım kapısını bulamadım.");
        iterator.setYanit("Yumurta");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Bin bir şekilli kitap.");
        iterator.setYanit("Boyama kitabı");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Başı topuz, saçı otuz.");
        iterator.setYanit("Süpürge");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Yağmur düşerken yükselen nedir?");
        iterator.setYanit("Şemsiye");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("İçi sert, dışı yumuşak.");
        iterator.setYanit("Zeytin");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Dalında yeşil, pazarda siyah, evde kırmızı olan şey nedir?");
        iterator.setYanit("Çay");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("İçini boşaltınca büyüyen şey nedir?");
        iterator.setYanit("Çukur");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Elden ele, telden tele, bunu bilmeyen kertenkele.");
        iterator.setYanit("Para");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Baş aşağı çevrilince değeri azalan şey nedir?");
        iterator.setYanit("Dokuz rakamı");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Eve anahtarsız girer.");
        iterator.setYanit("Duman");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Gez gez bitmez, o ölmez.");
        iterator.setYanit("Ayakkabı");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Burdan vurdum kılıcı, Halep'ten çıktı ucu.");
        iterator.setYanit("Şimşek");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("1 ağa, 9 köle");
        iterator.setYanit("Güneş ve Gezegenler");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Sakalı var dede değil, ormana gider avcı değil.");
        iterator.setYanit("Aslan");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Bir küçücük kumbara, erzak taşır ambara.");
        iterator.setYanit("Karınca");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Hareket etmeden neyimizi değiştiririz?");
        iterator.setYanit("Düşüncemizi");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Başkasına verdiğimiz halde bizim tutuğumuz şey nedir?");
        iterator.setYanit("Söz");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Kabuğu var, içi yok, dandin eder pekçok, herkesi seyre toplar, çomağı yer, suçu yok.");
        iterator.setYanit("Davul");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Paylaştıkça artar hiç bitmez acaba bu şey nedir?");
        iterator.setYanit("Sevgi");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Pazardan alınmaz, poşete konulmaz, tadına doyum olmaz.");
        iterator.setYanit("Uyku");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Kızgın gemi, dümdüz eder heryeri.");
        iterator.setYanit("Ütü");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Bir karış boyu var, hayvandan soyu var, oturmuş kendikendini yer ne kötü huyu var.");
        iterator.setYanit("Mum");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Hangi meslektekiler çalışırken sigara içemez?");
        iterator.setYanit("Dalgıçlar");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Eğri çınar, yerden alır, gökte yer.");
        iterator.setYanit("Deve");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Göz ile görülmez, el ile tutulmaz, bundan uzak duranlar sağlamdır hasta olmaz.");
        iterator.setYanit("Mikrop");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Bir kağıt parçası, içi parayla dolu.");
        iterator.setYanit("Çek");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Bir adam varmış, çölde tek başınaymış, başka hiçbir şey yokmuş bir şey içmiş. Ne içmiştir?");
        iterator.setYanit("And içmiş");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("O söyler ben dinlerim, ben söylerim o dinlemez. Bil bakalım ben kimim?");
        iterator.setYanit("Radyo");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Ne yerde ne gökte sepetin içinde Allah'ın emrinde.");
        iterator.setYanit("kısmet");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Ben varmadan o varır, her şeyden çok o yol alır.");
        iterator.setYanit("Işık");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Bir kafa kuzgun kuyruğu kendinden uzun.");
        iterator.setYanit("Tava");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Memleketim Mersin, her gün beni yersin.");
        iterator.setYanit("Portakal");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Öğütülür buğday değil, köpüğü var sabun değil.");
        iterator.setYanit("Kahve");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Yeter Çektiğim! diye yakınır.");
        iterator.setYanit("Fotoğraf makinesi");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Yol üstünde durur, gelene geçene buyurur.");
        iterator.setYanit("Trafik polisi");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Buzdolabına giren sineğe ne olur?");
        iterator.setYanit("Yazık olur");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Ayakta yetişen bitki nedir?");
        iterator.setYanit("Mantar");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Ayakkabıcıların en çok sevdiği hayvan hangisidir?");
        iterator.setYanit("Kırkayak");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Hangi bacalardan duman tütmez?");
        iterator.setYanit("Peri bacalarından");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Matematik kitabı Türkçe kitabına ne demiş?");
        iterator.setYanit("çok problemim var");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Hiç kimsenin okuyamadıgı yazı hangisidir?");
        iterator.setYanit("Alın yazısı");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Yarım elma neye benzer?");
        iterator.setYanit("Diğer yarısına");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("En son hangi dişler çıkar?");
        iterator.setYanit("Takma dişler");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Her tarafı sayılarla dolu olan adama ne denir?");
        iterator.setYanit("Numaracı");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Hangi karnede sıfır olmaz?");
        iterator.setYanit("Sağlık karnesinde");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Hangi yolda trafik kazası olmaz?");
        iterator.setYanit("Samanyolu'nda");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Tabanca ne ile temizlenir?");
        iterator.setYanit("Dikkatle");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Gözlemeyi en çok kim sever?");
        iterator.setYanit("Nöbetçi");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Düşünen file ne denir?");
        iterator.setYanit("Filozof");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Bozulduğu halde tamir edilmeyen şey nedir?");
        iterator.setYanit("Hava");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("İlk Türk bayrağını kim dikmiştir?");
        iterator.setYanit("Terzi");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Kimin önünde herkes şapka çıkarır?");
        iterator.setYanit("Berberin");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Arılar hangi kovana bal yapamazlar?");
        iterator.setYanit("Mermi kovanına");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Hiç kimsenin gitmek istemediği köy hangisidir?");
        iterator.setYanit("Tahtalıköy");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Çalmak fiilinin gelecek zamanı nedir?");
        iterator.setYanit("Hapse girmek");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Hangi devirde yastık savaşı yapılmaz?");
        iterator.setYanit("Taş devrinde");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Termometrenin düşmesi neyi gösterir?");
        iterator.setYanit("Çivinin iyi çakılmadığını");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Hangi kuyudan su içilmez?");
        iterator.setYanit("Petrol kuyusundan");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("İnsan ne yiyince üzülür?");
        iterator.setYanit("Kazık");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Ateş olmayan yerde ne olmaz?");
        iterator.setYanit("itfaiye");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Elemeden yoğurur, günaşırı doğurur.");
        iterator.setYanit("Tavuk");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Yük görünce kaçar, ot görünce koşar.");
        iterator.setYanit("Eşek");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Fırında pişer, mideye düşer.");
        iterator.setYanit("Ekmek");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("En kibar kuş hangisidir?");
        iterator.setYanit("Baykuş");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Kumsal denize ne demiş?");
        iterator.setYanit("benimle dalga geçme");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Kat kar açılır, kokusundan kaçılır?");
        iterator.setYanit("Soğan");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Toplamaz, çıkarmaz, sadece çarpar.");
        iterator.setYanit("Elektrik");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Hangi dolaba giysi koyulmaz?");
        iterator.setYanit("buz dolabına");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("En hızlı kıyafet hangisidir?");
        iterator.setYanit("Atlet");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("İnsanın gözü kapalı yaptığı en iyi şey nedir?");
        iterator.setYanit("Uyumak");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Duvara çarpan arabaya ne olur?");
        iterator.setYanit("Durur");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Hangi kanun insanları yargılamaz?");
        iterator.setYanit("yerçekimi kanunu");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Pijama giymiş eşeğe ne denir?");
        iterator.setYanit("iyi geceler");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Neron Roma'yı neden yakmıştır?");
        iterator.setYanit("üşüdüğü için");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Dünya'da en çok ne yenir?");
        iterator.setYanit("Ceza");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Bilgisayara niçin virüs girer?");
        iterator.setYanit("aşı olmadığı için");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Hangi tarlada meyve yetiştirilmez?");
        iterator.setYanit("Mayın tarlasında");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Küp şeker, toz şekere ne de demiş?");
        iterator.setYanit("biraz kendini topla demiş");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Yiyince bizi pişman eden tatlı nedir?");
        iterator.setYanit("Pişmaniye");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Sarıdır limon değil, ince uzun pırasa değil. Tadını sorsan bal gibi, şekli bir hilal gibi.");
        iterator.setYanit("Muz");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Gelirmi gelir, gitti mi gelmez.");
        iterator.setYanit("Gençlik");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Çarşıdan aldım bir tane eve geldim bin tane.");
        iterator.setYanit("Yapboz");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Uyudum yumuşak yattım sıcak sıcak.");
        iterator.setYanit("Yatak");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Güneşi arkana alırsan ne olur ?");
        iterator.setYanit("Gölge");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Bana sahipsin ama beni paylaşmak istersin ama beni paylaşırsan bana sahip olamazsın.");
        iterator.setYanit("Sır");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Bağlarım yürür, çözerim durur.");
        iterator.setYanit("Ayakkabı");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Uzundur ip değil, ısırır köpek değil.");
        iterator.setYanit("Yılan");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Zilim var, kapım yok.");
        iterator.setYanit("Telefon");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Bir sihirli fenerim, kibritsiz de yanarım.");
        iterator.setYanit("Ampul");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Bakması güzel, alması üzer.");
        iterator.setYanit("Gül");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Biz,  biz idik, otuz iki kız idik, ezildik, büzüldük, bir araya dizildik.");
        iterator.setYanit("Dişler");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("İnci gibi dişlerim, odunları keserim.");
        iterator.setYanit("Testere");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Ben iki hasretlinin arasında dururum. Onları konuştururum.");
        iterator.setYanit("Telefon");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("İnek verir bize, afiyet olsun diye.");
        iterator.setYanit("Süt");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Kıştan kaçmaz, yaprağı uçmaz.");
        iterator.setYanit("Çam ağacı");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Meyvelerin en güzeli, yem yeşil bahçe gibi.");
        iterator.setYanit("Erik");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Yedim yeşil bir külah, ağzım yandı vah vah.");
        iterator.setYanit("Biber");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Tarlada bitmez, saksıda bitmez, yerde var, gökte var, suda yok");
        iterator.setYanit("Hava");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Benim bir kaşık boyam var, dünyayı boyar.");
        iterator.setYanit("Güneş");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("İlkbaharda yeşildi rengim, sonbaharda sarardı.");
        iterator.setYanit("Yaprak");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Ben giderim, o gider, başımda gölge eder.");
        iterator.setYanit("Şemsiye");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Hava karardı, şimşek çaktı, her tarafı sel aldı.");
        iterator.setYanit("Yağmur");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Beyazdır kağıt değil, havada gezer uçak değil.");
        iterator.setYanit("Bulut");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Benim bir ağacım var, her gün bir yaprak döker.");
        iterator.setYanit("Takvim");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Biz 32 kardeşiz. 16'mız siyah 16'mız beyaz. Siyah beyaz bir tahtada toplantı yaparız.");
        iterator.setYanit("Satranç takımı");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Benim bir bahçem var içi suyla kaplı.");
        iterator.setYanit("Havuz");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Tuğladan yapısı, kiremitten şapkası.");
        iterator.setYanit("Ev");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Evin sıcak direği, pıt pıt eder yüreği.");
        iterator.setYanit("Baba");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Bizi çok sever anne baba değil, çalışırsan gözüne girersin, çoğunlukla onu görürsün.");
        iterator.setYanit("Öğretmen");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Su üşümüş ben olmuşum, güneş çıkınca yok olmuşum.");
        iterator.setYanit("Buz");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Ha iki teker ha üç teker, iki ayakla nasıl gider?");
        iterator.setYanit("Bisiklet");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Tereyağı, şerbet, fıstık, Yufka içine bastık, Dörtgen dilimler kestik.");
        iterator.setYanit("Baklava");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Cevap ağzında yoktur dişi, her gün yemektir işi.");
        iterator.setYanit("Bebek");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Yaz desem yazar, çiz desem çizer, resim yapmayı nede çok sever.");
        iterator.setYanit("Kalem");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Gider gider izi yok, burnu kara gözü yok.");
        iterator.setYanit("Gemi");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Çıt çıt yenir, adına eğlence denir.");
        iterator.setYanit("Çekirdek");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Yaşı yok, başı yok, adam olmuş, söz keser.");
        iterator.setYanit("Nokta");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Dalda sallanır, kazanda kaynar, her sabah masamızda bize el sallar.");
        iterator.setYanit("Reçel");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Yazılır üstüme, bilgiliyimdir de okullarda meşhurum da bul bakalım beni.");
        iterator.setYanit("Akıllı tahta");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Yerim yerim yitmez, karnıma gitmez.");
        iterator.setYanit("Sakız");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Beyaz elbiseli askerler, susayana su verirler.");
        iterator.setYanit("Bulutlar");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Kışta değil yazda, ocakta değil dolaptadır.");
        iterator.setYanit("Dondurma");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Uçar kelebek değil, beyazdır bulut değil.");
        iterator.setYanit("güvercin");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Her şeyi öğütür ama yorulmaz.");
        iterator.setYanit("Mide");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Tatlımı tatlı, yuvarlak bir kabın icinde saklı.");
        iterator.setYanit("Fındık");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Bilemezsin bildirir, düğmeleri vardır şekil şekil.");
        iterator.setYanit("Bilgisayar");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Yol gösteririm ama yoldan gidemem.");
        iterator.setYanit("Pusula");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Yeli var ççurmaz, akrep görse hiç durmaz?");
        iterator.setYanit("Yelkovan");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Yer üstünde yeşil kılıç.");
        iterator.setYanit("Ot");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Dağa çıktım çekiçle vurdum bütün mahalle duydu.");
        iterator.setYanit("Şimşek");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Kilitli bir kitap içinde özel şeyler yazar.");
        iterator.setYanit("Günlük");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Giy özel ayakkabılarını, çık buz pistine, ister dans et, ister yarış rüzgarla.");
        iterator.setYanit("Buz pateni");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Kopmuş kutuplardaki buzullardan. Bir buz parçası kocaman yüzer soğuk denizlerde durmadan.");
        iterator.setYanit("Buzdağı");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Yükselirim göklere bulutların üstüne, uçaklar geçer yanımdan duyarım kulaklarımla seslerini.");
        iterator.setYanit("Gökdelen");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Üç ayaklı tek gözlü?");
        iterator.setYanit("Teleskop");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Zıplar zıplar yerinden oynar.");
        iterator.setYanit("Kurbağa");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Ağaçlar çiçek açar, tüm hayvanlar yavrular, her yer yeşerir doğa şenlenir.");
        iterator.setYanit("İlkbahar");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Benim bir suyum var dökerim bizmez.");
        iterator.setYanit("Şelale");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Dokdor verdi, ben içtim, iyileştim.");
        iterator.setYanit("İlaç");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Bahçede durur hiç kıpırdamadan kargaları kovalar.");
        iterator.setYanit("Korkuluk");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Çıt çıt çalışır, kurulunca bağrışır.");
        iterator.setYanit("Çalar saat");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Rengi yeşil, çubukta yetişir. Hem taze hem de kuru yenir.");
        iterator.setYanit("Fasulye");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Anahtarla açılmayan kilit nedir?");
        iterator.setYanit("Tuş kilidi");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Telde yürür görünmez ev ev gezer erinmez?");
        iterator.setYanit("Elektrik");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("En temiz böcek hangisidir?");
        iterator.setYanit("Hamam böceği");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Ağzı küçük, midesi büyük.");
        iterator.setYanit("Çocuk");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("İletişim ağı, bağlan ona keşfet dünyayı!");
        iterator.setYanit("İnternet");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Kocaman bir toz ve buz topu, arkasında iz bırakır kuyruğu.");
        iterator.setYanit("Kuyrukluyıldız");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Yeni bağlantılar kurdukça içinde, bulmacaları da çözersin, en zor problemleri de!");
        iterator.setYanit("Beyin");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Termometredir ölçme aracı, kutuplarla ekvator arasında çok farlı!");
        iterator.setYanit("Sıcaklık");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Kıtaları birbirinden ayırır, gemilerle, uçaklarla aşılır!");
        iterator.setYanit("Okyanus");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Harekete karşı koyan kuvvet, durdurur seni sonunda elbet!");
        iterator.setYanit("Sürtünme kuvveti");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Eşi benzeri olmayan bir küre, Bir çok canlı yaşar üzerinde.");
        iterator.setYanit("Dünya");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Sihirlidir sanki, Koşturur peşinden toplu iğneleri.");
        iterator.setYanit("Mıknatıs");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Ampul, bilgisayar, uzay aracı, Hepsi onun marifeti, yaşamı kolaylaştırıcı!");
        iterator.setYanit("Teknoloji");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Damarlarımızın içinde dolaşır, besinler vücudumuzun her yerine onunla taşınır.");
        iterator.setYanit("Kan");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Sporların en eskilerinden biri, Tam da pehlivan işi, Yere yıkmak rakibi.");
        iterator.setYanit("Güreş");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Sallar birbirine yumrukları iki sporcu, Kalkamazsa on saniye yere düşen, Olur nakavtla maçı kaybeden.");
        iterator.setYanit("Boks");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Bir çok stili var: serbest, kelebek, Kurbağalama, sırüstü, karışık. Ne kadar hızlı atılırsa kulaçlar, O kadar kolay olur kazanmak.");
        iterator.setYanit("Yüzme");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Hem açar, hem kapar.");
        iterator.setYanit("Anahtar");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Yazın giyinir, kışın soyunur.");
        iterator.setYanit("Ağaç");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Bilmece bildirmece ayak altında kaydırmaca.");
        iterator.setYanit("Kayak");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Ben yazarım o bozar, doğruya gelmez zarar.");
        iterator.setYanit("Silgi");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Ateş olmayan yerde ne olmaz?");
        iterator.setYanit("İtfaiye");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Her gün yeniden doğar dünyaya haber yayar.");
        iterator.setYanit("Gazete");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Hangi teker dönmez?");
        iterator.setYanit("Yedek teker");
        bilmeceler.add(iterator);

        iterator = new Bilmece();
        iterator.setSoru("Horoz nerede öter?");
        iterator.setYanit("Kendi çöplüğünde");
        bilmeceler.add(iterator);




        /*

        NEW BILMECE CODE

        iterator = new Bilmece();
        iterator.setSoru("");
        iterator.setYanit("");
        bilmeceler.add(iterator);

        */

    }



}
