package fr.imt.boomeuuuuh.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import fr.imt.boomeuuuuh.Game;


public class BombeStandard extends Bomb {
    Texture sheetTexture = new Texture("Bomb/Clignx16.png"); // Image avec toutes les frames
    Texture explosion_verti = new Texture("Bomb/fire_verti.png");
    Texture explosion_horiz = new Texture("Bomb/fire_horiz.png");
    TextureRegion[][] tmp = TextureRegion.split(sheetTexture, 32, sheetTexture.getHeight()); // divise sheetTexture selon la taille de nos frames
    TextureRegion[] tabRegion = new TextureRegion[32]; // Tableau contenant nos frames
    int index = 0;
    boolean Clign = true;
    boolean feu = true;
    long t;
    Animation img = new Animation(0.25F, tabRegion);//Obj Animation qui contient le tableau de frame et le temps de chaque frame
    private TextureRegion regionCourante; // la frame actuelle, qui est une région de la sheet contenant toutes les frames
    int right=0,left=0,up=0,down=0;


    public BombeStandard(int id) {
        super(id);
        t = System.nanoTime();
        for (int j = 0; j < 32; j++) {
            tabRegion[index++] = tmp[0][j];
        } // on parcours les regions
    }

    public void draw(SpriteBatch batch, float temps) {
        if (Clign) {
            regionCourante = (TextureRegion) img.getKeyFrame(temps, false); // On choisi la region correspondant au temps t
            regionCourante.setRegion(regionCourante, 0, 0, 32, 32);// on set
            batch.draw(regionCourante, this.getX_screen(), this.getY_screen()); // on dessine cette image , aux coordonnées
        }

       if (System.nanoTime() - t > 3e9 & feu) {
                Clign = false;
                for (Entity E:Game.entities){
                    if(E instanceof Bloc){
                        if(E.getY()==this.getY()){
                            if( (this.getX()-power<= E.getX()) & (E.getX()<=this.getX()-1) ){
                                left = Math.max(left,E.getX());
                            }
                            if( (this.getX()+power>= E.getX()) & (E.getX()>=this.getX()+1) ){
                                right = Math.min(right,E.getX());
                            }
                        }
                        if(E.getX()==this.getX()){
                            if( (this.getY()-power<= E.getY()) & (E.getY()<=this.getY()-1)){
                                down = Math.max(down,E.getY());
                            }
                            if( (this.getY()+power>= E.getY()) & (E.getY()>=this.getY()+1) ){

                                up = Math.min(up,E.getY());
                            }
                        }
                    }
                }

                for (int k = 0; k < this.power; k++) {
                    if(this.getX() + k<= Math.min(this.getX()+power,right)) {
                        batch.draw(explosion_horiz, this.getX_screen() + (32 * k), this.getY_screen());
                    }
                    if(this.getX() - k>= Math.max(this.getX()-power,left)) {
                        batch.draw(explosion_horiz, this.getX_screen() - (32 * k), this.getY_screen());
                    }
                    if(this.getY() + k<= Math.min(this.getY()+power,up)) {
                        batch.draw(explosion_verti, this.getX_screen(), this.getY_screen() + (32 * k));
                    }
                    if(this.getY() - k>= Math.max(this.getY()-power,down)) {
                        batch.draw(explosion_verti, this.getX_screen(), this.getY_screen() - (32 * k));
                    }


                    }

                if(System.nanoTime()-t>35e8){
                        feu=false;
                    }

                }


      }

 }





