package edu.caltech.cs2.project01;

import java.util.Map;
import java.util.Random;
import java.util.HashMap;
import java.io.FileNotFoundException;

public class SubstitutionCipher {
    private String ciphertext;
    private Map<Character, Character> key;

    // Use this Random object to generate random numbers in your code,
    // but do not modify this line.
    private static final Random RANDOM = new Random();

    /**
     * Construct a SubstitutionCipher with the given cipher text and key
     * @param ciphertext the cipher text for this substitution cipher
     * @param key the map from cipher text characters to plaintext characters
     */
    public SubstitutionCipher(String ciphertext, Map<Character, Character> key) {
        this.ciphertext = ciphertext;
        this.key = key;
    }

    /**
     * Construct a SubstitutionCipher with the given cipher text and a randomly
     * initialized key.
     * @param ciphertext the cipher text for this substitution cipher
     */
    public SubstitutionCipher(String ciphertext) {
        // get identity map first
        Map<Character, Character> key = new HashMap<>(genIdentityMap());

        //declare and initialize SubstitutionCipher
        SubstitutionCipher cipher = new SubstitutionCipher(ciphertext, key);

        // mutate SubstitutionCipher 10000 times
        for (int i = 0; i < 10000; i++) {
            cipher = cipher.randomSwap();
        }

        this.ciphertext = ciphertext;
        this.key = cipher.key;
    }

    /**
     * Returns the unedited cipher text that was provided by the user.
     * @return the cipher text for this substitution cipher
     */
    public String getCipherText() {
        return this.ciphertext;
    }

    /**
     * Applies this cipher's key onto this cipher's text.
     * That is, each letter should be replaced with whichever
     * letter it maps to in this cipher's key.
     * @return the resulting plain text after the transformation using the key
     */
    public String getPlainText() {
        // declare plainText, which we will return eventually
        StringBuilder plainText = new StringBuilder();


        // loop through each char of ciphertext
        // use this.key to map each char to the right char in plainText
        if (this.ciphertext != null && this.key != null){
            for (int i = 0; i < this.ciphertext.length(); i++) {
                plainText.append(this.key.get(this.ciphertext.charAt(i)));
            }
        }
        String plainTextString = plainText.toString();
        return plainTextString;
    }

    /**
     * Returns a new SubstitutionCipher with the same cipher text as this one
     * and a modified key with exactly one random pair of characters exchanged.
     *
     * @return the new SubstitutionCipher
     */
    public SubstitutionCipher randomSwap() {

        // declare new key
        Map<Character, Character> newKey = new HashMap<>();

        // deep copy from the current key
        for (char letter : CaesarCipher.ALPHABET){
            newKey.put(letter, key.get(letter));
        }

        // declare / initialize first rando number
        int numOne = RANDOM.nextInt(26);
        // declare / initialize second rando number
        int numTwo = RANDOM.nextInt(26);

        // make sure the two numbers are different
        while (numOne == numTwo) {
            //generate random number 2
            numTwo = RANDOM.nextInt(26);
        }


        // access ALPHABET from CaesarCipher class
        char letterOne = CaesarCipher.ALPHABET[numOne];
        char letterTwo = CaesarCipher.ALPHABET[numTwo];


        // swap the corresponding letters in key
        newKey.remove(letterOne);
        newKey.remove(letterTwo);
        newKey.put(letterOne, key.get(letterTwo));
        newKey.put(letterTwo, key.get(letterOne));

        // return SubstitutionCipher with same ciphertext, new key
        return new SubstitutionCipher(this.ciphertext, newKey);
    }


    public static Map<Character, Character> genIdentityMap() {
        return Map.ofEntries(Map.entry('A', 'A'),
                Map.entry('B', 'B'),
                Map.entry('C', 'C'),
                Map.entry('D', 'D'),
                Map.entry('E', 'E'),
                Map.entry('F', 'F'),
                Map.entry('G', 'G'),
                Map.entry('H', 'H'),
                Map.entry('I', 'I'),
                Map.entry('J', 'J'),
                Map.entry('K', 'K'),
                Map.entry('L', 'L'),
                Map.entry('M', 'M'),
                Map.entry('N', 'N'),
                Map.entry('O', 'O'),
                Map.entry('P', 'P'),
                Map.entry('Q', 'Q'),
                Map.entry('R', 'R'),
                Map.entry('S', 'S'),
                Map.entry('T', 'T'),
                Map.entry('U', 'U'),
                Map.entry('V', 'V'),
                Map.entry('W', 'W'),
                Map.entry('X', 'X'),
                Map.entry('Y', 'Y'),
                Map.entry('Z', 'Z'));
    }

    /**
     * Returns the "score" for the "plain text" for this cipher.
     * The score for each individual quadgram is calculated by
     * the provided likelihoods object. The total score for the text is just
     * the sum of these scores.
     * @param likelihoods the object used to find a score for a quadgram
     * @return the score of the plain text as calculated by likelihoods
     */
    public double getScore(QuadGramLikelihoods likelihoods) {
        //get plainText
        String plainText = getPlainText();

        //rolling sum
        double sumLogLikelihoods = 0;

        // get each quadgram with substring method
        // find likelihood for each quadgram and add to rolling sum
        for (int i = 0; i <= plainText.length() - 4; i++){
            sumLogLikelihoods += likelihoods.get(
                    plainText.substring(i, i + 4)
            );
        }
        return sumLogLikelihoods;
    }

    /**
     * Attempt to solve this substitution cipher through the hill
     * climbing algorithm. The SubstitutionCipher this is called from
     * should not be modified.
     * @param likelihoods the object used to find a score for a quadgram
     * @return a SubstitutionCipher with the same ciphertext and the optimal
     *  found through hill climbing
     */
    public SubstitutionCipher getSolution(QuadGramLikelihoods likelihoods) {
        // Create a cipher with this cipher’s ciphertext and a random key
        SubstitutionCipher cipher = new SubstitutionCipher(ciphertext);
        SubstitutionCipher newCipher;

//        While fewer than 1000 trials in a row have not resulted in a replacement:
//        Randomly swap two letters in C
//        to make a new key M; desc taken from pset
        int i = 1;
        while(i < 1000){
            newCipher = cipher.randomSwap();
            if (newCipher.getScore(likelihoods) > cipher.getScore(likelihoods)){
                cipher = newCipher;
                i = 1;
            }
            else{
                i += 1;
            }
        }

        return cipher;
    }



    public static void main(String[] args) throws FileNotFoundException {

        SubstitutionCipher cipher = new SubstitutionCipher("YZSGQNLGBKCNVVILYNNGDQNLQBFCNVVRNLSRGQLGBKLGQNMVBRYCNVVILYNNGDQNLQBFYZYNHMZYGQBKLGQZLNLGBKLSNKNYGLZCRPGQNISNKNKNBVVILZLXBVVIZHXRPQGGQRYULHDQBGQRYPSZHVFYGXBGGNKBGBVVCHGCNDBHLNGQNIQBFLGBKLBVVGQNLGBKCNVVILYNNGDQNLSZHVFCKBPSNKNGQNCNLGURYFZOLYNNGDQZYGQNCNBDQNLSRGQGQNRKLYZZGLRYGQNBRKGQNISZHVFLYROOBYFGQNIFLYZKGSNVVQBENYZGQRYPGZFZSRGQGQNMVBRYCNVVILZKGBYFSQNYNENKGQNIXNGLZXNSQNYGQNISNKNZHGSBVURYPGQNIFQRUNKRPQGZYMBLGGQNXSRGQZHGNENYGBVURYPSQNYGQNLGBKCNVVIDQRVFKNYSNYGZHGGZMVBICBVVDZHVFBMVBRYCNVVIPNGRYGQNPBXNYZGBGBVVIZHZYVIDZHVFMVBIROIZHKCNVVRNLQBFLGBKLBYFGQNMVBRYCNVVIDQRVFKNYQBFYZYNHMZYGQBKLSQNYGQNLGBKCNVVILYNNGDQNLQBFOKBYUOHKGNKKZBLGLZKMRDYRDLZKMBKGRNLZKXBKLQXBVVZSGZBLGLGQNIYNENKRYERGNFGQNMVBRYCNVVILYNNGDQNLGQNIVNOGGQNXZHGDZVFRYGQNFBKUZOGQNCNBDQNLGQNIUNMGGQNXBSBIYNENKVNGGQNXDZXNYNBKBYFGQBGLQZSGQNIGKNBGNFGQNXINBKBOGNKINBKGQNYZYNFBIRGLNNXLSQRVNGQNMVBRYCNVVILYNNGDQNLSNKNXZMRYPBYFFZMRYPBVZYNZYGQNCNBDQNLTHLGLRGGRYPGQNKNSRLQRYPGQNRKCNVVRNLQBFLGBKLBLGKBYPNKARMMNFHMRYGQNLGKBYPNLGZODBKLXIOKRNYFLQNBYYZHYDNFRYBEZRDNDVNBKBYFUNNYXIYBXNRLLIVENLGNKXDXZYUNIXDCNBYBYFRENQNBKFZOIZHKGKZHCVNLRENQNBKFIZHKNHYQBMMICHGRDBYORWGQBGRXGQNORWRGHMDQBMMRNRENDZXNQNKNGZQNVMIZHRQBENSQBGIZHYNNFBYFXIMKRDNLBKNVZSBYFRSZKUBGPKNBGLMNNFBYFXISZKURLZYNQHYFKNFMNKDNYGPHBKBYGNNFGQNYJHRDUVILIVENLGNKXDXZYUNIXDCNBYMHGGZPNGQNKBENKIMNDHVRBKXBDQRYNBYFQNLBRFIZHSBYGLGBKLVRUNBLGBKCNVVILYNNGDQXIOKRNYFLIZHDBYQBENGQNXOZKGQKNNFZVVBKLNBDQTHLGMBIXNIZHKXZYNIBYFQZMKRPQGBCZBKFLZGQNIDVBXCNKNFRYLRFNGQNYGQNCRPXBDQRYNKZBKNFBYFRGUVZYUNFBYFRGCZYUNFBYFRGTNKUNFBYFRGCNKUNFBYFRGCZMMNFGQNXBCZHGCHGGQNGQRYPKNBVVISZKUNFSQNYGQNMVBRYCNVVILYNNGDQNLMZMMNFZHGGQNIQBFLGBKLGQNIBDGHBVVIFRFGQNIQBFLGBKLHMZYGQBKLGQNYGQNIINVVNFBGGQNZYNLSQZQBFLGBKLBGGQNLGBKGSNKNNWBDGVIVRUNIZHIZHDBYGGNVVHLBMBKGSNKNBVVTHLGGQNLBXNYZSIZHLYZZGIZVFLXBKGRNLBYFYZSSNDBYPZGZIZHKOKBYUOHKGNKMBKGRNLPZZFPKRNOPKZBYNFGQNZYNLSQZQBFLGBKLBGGQNORKLGSNKNLGRVVGQNCNLGLYNNGDQNLBYFGQNIBKNGQNSZKLGCHGYZSQZSRYGQNSZKVFSRVVSNUYZSGQNIBVVOKZSYNFROSQRDQURYFRLSQBGZKGQNZGQNKSBIKZHYFGQNYHMDBXNXDCNBYSRGQBENKILVISRYUBYFQNLBRFGQRYPLBKNYZGJHRGNBLCBFBLIZHGQRYULZIZHFZYGUYZSSQZLSQZGQBGRLMNKONDGVIGKHNCHGDZXNSRGQXNOKRNYFLFZIZHUYZSSQBGRVVFZRVVXBUNIZHBPBRYGQNCNLGLYNNGDQNLZYGQNCNBDQNLBYFBVVRGSRVVDZLGIZHRLGNYFZVVBKLNBDQNLCNVVILGBKLBKNYZVZYPNKRYLGIVNLBRFXDCNBYSQBGIZHYNNFRLBGKRMGQKZHPQXILGBKZOOXBDQRYNGQRLSZYFKZHLDZYGKBMGRZYSRVVGBUNZOOIZHKLGBKLLZIZHSZYGVZZUVRUNLYNNGDQNLSQZQBENGQNXZYGQBKLBYFGQBGQBYFIXBDQRYNSZKURYPENKIMKNDRLNVIKNXZENFBVVGQNLGBKLOKZXGQNRKGHXXRNLJHRGNYRDNVIGQNYSRGQLYZZGLRYGQNBRKGQNIMBKBFNFBCZHGBYFGQNIZMNYNFGQNRKCNBULBYFGQNIVNGZHGBLQZHGSNUYZSSQZRLSQZYZSGQNKNRLYGBFZHCGGQNCNLGURYFZOLYNNGDQNLBKNLYNNGDQNLSRGQZHGGQNYZODZHKLNGQZLNSRGQLGBKLPZGOKRPQGOHVVIXBFGZCNSNBKRYPBLGBKYZSSBLOKRPQGOHVVICBFGQNYZODZHKLNZVFLIVENLGNKXDXZYUNIXDCNBYRYERGNFGQNXRYGZQRLLGBKZOOXBDQRYNGQNYZODZHKLNOKZXGQNYZYBLIZHMKZCBCVIPHNLLGQRYPLKNBVVIPZGRYGZBQZKKRCVNXNLLBVVGQNKNLGZOGQBGFBIZYGQZLNSRVFLDKNBXRYPCNBDQNLGQNORWRGHMDQBMMRNUNMGORWRYPHMLYNNGDQNLZOOBPBRYZYBPBRYRYBPBRYZHGBPBRYGQKZHPQGQNXBDQRYNLGQNIKBDNFKZHYFBYFBCZHGBPBRYDQBYPRYPGQNRKLGBKLNENKIXRYHGNZKGSZGQNIUNMGMBIRYPXZYNIGQNIUNMGKHYYRYPGQKZHPQHYGRVYNRGQNKGQNMVBRYYZKGQNLGBKCNVVRNLUYNSSQNGQNKGQRLZYNSBLGQBGZYNZKGQBGZYNSBLGQRLZYNZKSQRDQZYNSBLSQBGZYNZOSQBGZYNSBLSQZGQNYSQNYNENKIVBLGDNYGZOGQNRKXZYNISBLLMNYGGQNORWRGHMDQBMMRNMBDUNFHMBYFQNSNYGBYFQNVBHPQNFBLQNFKZENRYQRLDBKHMGQNCNBDQGQNIYNENKSRVVVNBKYYZIZHDBYGGNBDQBLYNNGDQCHGXDCNBYSBLJHRGNSKZYPRXJHRGNQBMMIGZLBIGQBGGQNLYNNGDQNLPZGKNBVVIJHRGNLXBKGZYGQBGFBIGQNFBIGQNIFNDRFNFGQBGLYNNGDQNLBKNLYNNGDQNLBYFYZURYFZOLYNNGDQRLGQNCNLGZYGQNCNBDQNLGQBGFBIBVVGQNLYNNGDQNLOZKPZGBCZHGLGBKLBYFSQNGQNKGQNIQBFZYNZKYZGHMZYGQBKL");
        QuadGramLikelihoods qgl = new QuadGramLikelihoods();
        cipher = cipher.getSolution(qgl);
        System.out.println(cipher.getPlainText());
    }
}


