#include <iostream>
#include <stdio.h>
#include <string>
#include <map>
#include "algorithmes_tri.h"
#include "fichier.h"
using namespace std;

int main()
{
    int n, choixInput,choixAlgorithme, countSwaps, countComparisons;
    map<string,int> res;
    char save;
    int * tab;
    cout << "*** Implementation des algorithmes de tri en C++ ***\n";    
    cout<<"-------------------\n";
    do {
        cout<< "Veuillez choisir votre methode d'input parmi les 2 choix:\n";
        cout<< "1: Charger le tableau a partir d'un fichier\n";
        cout<< "2: Entrer le tableau manuellement\n";
        cout<< "0: Quitter le programme\n";
        cin>> choixInput;
        switch(choixInput){
            case 1:{
                string filename;
                char separateur;
                cout<< "Saisissez le nom du fichier:\t";
                cin>> filename;
                cout <<"\nchoisissez un separateur:\t";
                cin>> separateur;
                tab = fileToArr(filename,separateur, n);
                break;

            }
            case 2:{
                cout << "Veuillez saisir la taille du tableau\n";
                cin >> n;
                tab = new int [n];
                for (int i=0;i<n;i++){
                    cout << "Veuillez saisir l'element "<< i+1 << " du tableau : \n";
                    cin >> tab[i];
                }
                break;
            }
            case 0:
                cout<< "Fin du programme...\n";
                return 0;
                break;
            default:
                cout<< "Choix invalide...\n";
                break;
        }
    }while(choixInput !=1 && choixInput !=2);

    int * newTab = new int [n];
    cout<<"-------------------\n";
    do{
        cout << "\nVeuillez choisir un algorithme de tri parmi la liste :\n\n";
        cout << "1:selection \t 2:insertion \t 3:bulle \t 4:peigne \t 5:shaker\n";
        cout << "6:shell \t 7:gnome \t 8:maximier \t 9:fusion \t 10:rapide\n";
        cout<< "\n0:quitter\n";
        cin >> choixAlgorithme;
        if (choixAlgorithme!=0){
            cout << "affichage du tableau avant tri \n";
            printArray(tab, n);
        }
        cout<<"-------------------\n";
        newTab = copyTab(tab,  n);
        countSwaps=0;countComparisons=0;
        switch(choixAlgorithme){
            case 1:
                selectionSort(newTab, n,countComparisons, countSwaps);
                res["selection"]=countSwaps;
                break;
            case 2:
                insertionSort(newTab, n,countComparisons, countSwaps);
                res["insertion"]=countSwaps;
                break;
            case 3:
                bubbleSort(newTab, n,countComparisons, countSwaps);
                res["bulle"]=countSwaps;
                break;
            case 4:
                combSort(newTab, n,countComparisons, countSwaps);
                res["peigne"]=countSwaps;
                break;
            case 5:
                CocktailSort(newTab, n,countComparisons, countSwaps);
                res["shaker"]=countSwaps;
                break;
            case 6:
                shellSort(newTab, n,countComparisons, countSwaps);
                res["shell"]=countSwaps;
                break;
            case 7:
                gnomeSort(newTab, n,countComparisons, countSwaps);
                res["shell"]=countSwaps;
                break;
            case 8:
                heapSort(newTab, n,countComparisons, countSwaps);
                res["maximier"]=countSwaps;
                break;
            case 9:
                mergeSort(newTab,0, n-1,countComparisons, countSwaps);
                res["fusion"]=countSwaps;
                break;
            case 10:
                quickSort(newTab, 0,  n-1,countComparisons, countSwaps);
                res["rapide"]=countSwaps;
                break;
            case 0:
                cout<< "Fin Programme...\n";
                break;
            default:
                cout<<"Choix invalide";
                break;
        }
        cout<<"-------------------\n";
        if (choixAlgorithme!=0){
            cout << "affichage du tableau apres tri \n";
            printArray(newTab, n);
            cout<<"permutations: "<< countSwaps<<endl;
            cout << "Voulez-vous enregistrer le tableau apres tri dans un fichier? [y/n]\n";
            cin >> save;
            if(save=='y'){
                string filename;
                char separateur;
                cout<< "Saisissez le nom du fichier:\t";
                cin>> filename;
                cout <<"\nchoisissez un separateur:\t";
                cin>> separateur;
                arrToFile(filename, newTab,n,separateur);
            }

        }
    } while(choixAlgorithme != 0);
    map<string,int>::iterator it;

    for(it = res.begin();it != res.end();it++){
        cout<<"algorithme: "<<it->first<<"\tpermutations: "<<it->second<<endl;
    }
    return (0);
}

