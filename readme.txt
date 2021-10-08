To run the program you need Java executable environment version minimum 1.4.

Run the program with the file "szyfrRsa.jar" or compile and run the file "SzyfrRsa.java"

In the program you should first generate a key and save it on disk:
- public key with the extension "*.kpu"
- private key with the extension "*.kpr"

According to the RSA cipher algorithm, in order to encode a text file you should:
1. from the Operation menu select Encryption options
2. from the Key menu select Load Public Key and enter the key file with the extension "*.kpu" - the name of the key file will appear in the Key File frame;
3. enter or insert the name of the text file to be encrypted in the Source File frame - the name of the loaded file with the extension "*.kod" will automatically appear in the Target File frame
4. after pressing the Encrypt key, wait for a while depending on the file size
 
Decryption operations should be performed in the following order
1. from the Operation menu select Decryption option
2. from the Key menu select Load Private Key and enter the key file with the extension "*.kpr" - the name of the key file will appear in the Key File frame;
3. enter or insert the name of the encrypted file with the extension "*.kod" into the Source File frame and enter the name of the output file into the Target File frame
4. after pressing the Decrypt key you should wait a while depending on the size of the file.

Translated with www.DeepL.com/Translator (free version)
