Imports System.IO
Imports System.Collections.Generic
Imports System.Linq
Imports System.Text
Imports System.Collections

Class Program
    Private strLine As String, strLine1 As String
    Public Shared count As Integer, count1 As Integer
    Private i As Integer = 0

    Public Function reading(name As String, newname As String) As Integer
        Dim out(3) As Integer
        count = 0
        count1 = 0
        Dim aFile As New FileStream(name, FileMode.Open, FileAccess.Read)
        Dim sr As New StreamReader(aFile)
        Dim aFile1 As New FileStream(newname, FileMode.Open, FileAccess.Read)
        Dim sr1 As New StreamReader(aFile1)
        Dim flag, ncount, dcount As Integer
        ncount = 0
        dcount = 0
        flag = 0
        Do While sr1.Peek <> -1
            strLine = sr1.ReadLine
            flag = 0
            Do While sr.Peek <> -1
                strLine1 = sr.ReadLine
                If strLine.Equals(strLine1) = 0 Then
                    flag = 1
                    Exit Do
                End If
            Loop
            If flag = 0 Then
                ncount = ncount + 1
            End If
        Loop

        Return ncount
    End Function
End Class

'   Class exec
' Private Shared Sub Main(args As String())
'Dim p As New Program()
'   Console.WriteLine("Enter Old file name")
'Dim fname As String = Console.ReadLine()
'   Console.WriteLine("Enter Changed Version file name")
'Dim fnewname As String = Console.ReadLine()
'   p.reading(fname, fnewname)
'  Console.WriteLine("Hi Cooooool")
'Dim nnnn As String = Console.ReadLine()
'End Sub
'End Class







